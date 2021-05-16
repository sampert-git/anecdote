package com.springboot.anecdote.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.springboot.anecdote.entity.Anecdote;
import com.springboot.anecdote.entity.Comment;
import com.springboot.anecdote.entity.User;
import com.springboot.anecdote.service.AnecdoteService;
import com.springboot.anecdote.service.CommentService;
import com.springboot.anecdote.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 轶事（Anecdote）控制器
 *
 * @author Sampert
 * @version 1.0
 * @date 2020/9/12 9:25
 **/
@Controller
public class AnecdoteController {

    private AnecdoteService anecdoteService;
    private CommentService commentService;
    private UserService userService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int PAGE_SIZE = 12;
    private static final Logger LOGGER = LoggerFactory.getLogger(AnecdoteController.class);

    @Autowired
    public AnecdoteController(AnecdoteService anecdoteService, UserService userService, CommentService commentService) {
        this.anecdoteService = anecdoteService;
        this.commentService = commentService;
        this.userService = userService;
    }

    // 查找Anecdotes列表（keyword条件可选）
    @GetMapping(value = {"/anec/list/{pageNum}/{target}/{keyword}", "/anec/list/{pageNum}/{target}"})
    public String listAnecdotes(@PathVariable("pageNum") Integer pageNum, Model model, HttpSession session,
                                    @PathVariable("target") String target,
                                    @PathVariable(value = "keyword", required = false) String keyword,
                                    @CookieValue(value = "anec_login_account", required = false) String account) {
        // 如果客户端浏览器存在账户cookie且还未创建session则根据cookie存储的帐户创建一个session
        if (account != null && session.getAttribute("ANEC_USER_SESSION") == null) {
            User user = userService.getUserByAccount(account);
            session.setAttribute("ANEC_USER_SESSION", user);
            LOGGER.info("用户：{}（id={}）通过cookie登录成功！", user.getUserName(), user.getUserId());
        }
        // 装配分页信息
        PageHelper.startPage(pageNum, PAGE_SIZE);
        List<Anecdote> anecdotes = anecdoteService.listAnecdotes(keyword, pageNum);
        PageInfo<Anecdote> pageInfo = new PageInfo<>(anecdotes);
        model.addAttribute("pageInfo", pageInfo);
        if (keyword != null)
            model.addAttribute("title", "搜索结果");
        else
            model.addAttribute("title", "Anecdote 名人轶事");
        return "client".equals(target) ? "anec-list" : "admin";
    }

    // 根据id查找Anecdote（返回HTML模板）
    @GetMapping("/anec/id/{id}")
    public String getAnecdoteById(@PathVariable("id") Integer id, Model model) {
        Anecdote anecdote = anecdoteService.getAnecdoteById(id);
        // 装配评论列表
        List<Comment> comments = commentService.findCommsByAnecId(anecdote.getAnecId());
        for (Comment comment : comments) {
            // 评论人姓名
            comment.setUserName(userService.getUserNameById(comment.getUserId()));
            // 要减去保存评论时增加的8小时
            comment.setTimeStr(comment.getCrateTime().minusHours(8).format(FORMATTER));
        }
        anecdote.setComments(comments);
        model.addAttribute("anecdote", anecdote);
        return "anec-details";
    }

    // 根据id查找Anecdote（返回json数据）
    @GetMapping("/admin/anec/json/id/{id}")
    @ResponseBody
    public Anecdote getJsonAnecdoteById(@PathVariable("id") Integer id) {
        return anecdoteService.getAnecdoteById(id);
    }

    // 根据创建人id查找Anecdotes(需要单独再写一个HTML页面，内容同anec-list.html,仅分页链接属性不同)
    @GetMapping("/client/anec/list/{pageNum}/createId/{createId}")
    public String listAnecdotesByCreUser(@PathVariable("pageNum") Integer pageNum, Model model,
                                     @PathVariable("createId") Integer createId) {
        PageHelper.startPage(pageNum, PAGE_SIZE);
        List<Anecdote> anecdotes = anecdoteService.listAnecdotesByCreUser(createId);
        PageInfo<Anecdote> pageInfo = new PageInfo<>(anecdotes);
        model.addAttribute("pageInfo", pageInfo);
        return "anec-list";
    }

    // 添加Anecdote（Anecdote需携带信息：anecPerson,anecTitle,anecContent）
    @PostMapping("/client/anec/add")
    @ResponseBody
    public String saveAnecdote(@RequestPart("anecdote") Anecdote anecdote, HttpSession session,
                              @RequestPart(value = "file", required = false) MultipartFile file) {
        User user = (User) session.getAttribute("ANEC_USER_SESSION");
        anecdote.setAnecCreateId(user.getUserId());
        anecdote.setAnecCreateTime(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));
        int result = anecdoteService.saveAnecdote(anecdote, file);
        return result > 0 ? "ok" : "fail";
    }

    // 更新Anecdote
    @PostMapping("/admin/anec/update/{pageNum}")
    @ResponseBody
    public String updateAnecdote(Anecdote anecdote, @PathVariable("pageNum") Integer pageNum) {
        int result = anecdoteService.updateAnecdote(anecdote, pageNum);
        return result > 0 ? "ok" : "fail";
    }

    // 删除Anecdote
    @GetMapping("/admin/anec/delete/{anecId}/{creUserId}/{anecImgPath}")
    @ResponseBody
    public String deleteAnecdote(@PathVariable("anecId") Integer anecId,
                                 @PathVariable("creUserId") Integer creUserId,
                                 @PathVariable("anecImgPath") String anecImgPath) {
        int result = anecdoteService.deleteAnecdote(anecId, creUserId);
        if (result > 0) {
            // 删除Anecdote对应Comment列表
            commentService.deleteCommList(anecId);
            // 删除Anecdote对应图片（anecImgPath 形如 xxx.jpg）
            if (!anecdoteService.deleteAnecImg(anecImgPath)) {
                // 图片删除失败的处理……
                LOGGER.warn("图片删除失败！anecId = {}, anecImgPath = {}", anecId, anecImgPath);
            }
            return "ok";
        } else
            return "fail";
    }
}
