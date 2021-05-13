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
 * Class AnecdoteController 
 * Description //TODO Anecdote控制器类；
 * Date 2020/9/12 9:25
 * @author Sampert
 * @version 1.0
 **/
@Controller
public class AnecdoteController {

    private AnecdoteService anecdoteService;
    private CommentService commentService;
    private UserService userService;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Logger LOGGER = LoggerFactory.getLogger(AnecdoteController.class);

    @Autowired
    public AnecdoteController(AnecdoteService anecdoteService,UserService userService,CommentService commentService) {
        this.anecdoteService = anecdoteService;
        this.commentService = commentService;
        this.userService = userService;
    }

    /*
     * Anecdote（轶事）操作
     * */

    // 查找Anecdotes列表（keyword条件可选）
    @GetMapping(value = {"/anec/list/{pageNum}/{target}/{keyword}","/anec/list/{pageNum}/{target}"})
    public String findListAnecdotes(@PathVariable("pageNum") Integer pageNum, Model model, HttpSession session,
                                    @PathVariable("target") String target,
                                    @PathVariable(value = "keyword",required = false) String keyword,
                                    @CookieValue(value = "anec_login_account",required = false) String account){
        // 如果客户端浏览器存在账户cookie且还未创建session则根据cookie存储的帐户创建一个session
        if (account != null && session.getAttribute("ANEC_USER_SESSION") == null) {
            User user=userService.getUserByAccount(account);
            session.setAttribute("ANEC_USER_SESSION", user);
            LOGGER.info("用户"+user.getUserName()+"(id="+user.getUserId()+")通过cookie登录成功！");
        }
        // 装配分页信息
        PageHelper.startPage(pageNum,12);
        List<Anecdote> anecdotes=anecdoteService.findListAnecdotes(keyword,pageNum);
        PageInfo<Anecdote> pageInfo=new PageInfo<>(anecdotes);
        model.addAttribute("pageInfo",pageInfo);
        if (keyword!=null)
            model.addAttribute("title","搜索结果");
        else
            model.addAttribute("title","Anecdote 名人轶事");
        return "client".equals(target) ? "anec-list" : "admin";
    }

    // 根据id查找Anecdote（返回HTML模板）
    @GetMapping("/anec/id/{id}")
    public String findAnecdoteById(@PathVariable("id") Integer id, Model model){
        Anecdote anecdote=anecdoteService.findAnecdoteById(id);
        // 装配评论列表
        List<Comment> comments = commentService.findCommsByAnecId(anecdote.getAnecId());
        for (Comment comment:comments){
            // 装配每条Comment评论人名称,格式化日期时间
            comment.setUserName(userService.findUserNameById(comment.getUserId()));
            comment.setTimeStr(comment.getCrateTime().format(formatter));
        }
        anecdote.setComments(comments);
        model.addAttribute("anecdote",anecdote);
        return "anec-details";
    }

    // 根据id查找Anecdote（返回json数据）
    @GetMapping("/admin/anec/json/id/{id}")
    @ResponseBody
    public Anecdote findJsonAnecdoteById(@PathVariable("id") Integer id){
        return anecdoteService.findAnecdoteById(id);
    }

    // 根据创建人id查找Anecdotes(需要单独再写一个HTML页面，内容同anec-list.html,仅分页链接属性不同)
    @GetMapping("/client/anec/list/{pageNum}/createId/{createId}")
    public String findAnecsByCreUser(@PathVariable("pageNum") Integer pageNum,Model model,
                                     @PathVariable("createId") Integer createId){
        PageHelper.startPage(pageNum,12);
        List<Anecdote> anecdotes=anecdoteService.findAnecsByCreUser(createId);
        PageInfo<Anecdote> pageInfo=new PageInfo<>(anecdotes);
        model.addAttribute("pageInfo",pageInfo);
        return "anec-list";
    }

    // 添加Anecdote（Anecdote需携带信息：anecPerson,anecTitle,anecContent）
    @PostMapping("/client/anec/add")
    @ResponseBody
    public String addAnecdote(@RequestPart("anecdote") Anecdote anecdote,HttpSession session,
                              @RequestPart(value = "file",required = false) MultipartFile file){
        User user=(User) session.getAttribute("ANEC_USER_SESSION");
        anecdote.setAnecCreateId(user.getUserId());
        int result=anecdoteService.addAnecdote(anecdote,file);
        return result > 0 ? "ok" : "fail";
    }

    // 更新Anecdote
    @PostMapping("/admin/anec/update/{pageNum}")
    @ResponseBody
    public String updateAnecdote(Anecdote anecdote,@PathVariable("pageNum") Integer pageNum){
        int result=anecdoteService.updateAnecdote(anecdote,pageNum);
        return result > 0 ? "ok" : "fail";
    }

    // 删除Anecdote
    @GetMapping("/admin/anec/delete/{id}/creUserId/{creUserId}")
    @ResponseBody
    public String deleteAnecdote(@PathVariable("id") Integer id,
                                 @PathVariable("creUserId") Integer creUserId){
        int result=anecdoteService.deleteAnecdote(id,creUserId);
        if (result>0){
            // 删除被删Anecdote对应Comment列表
            commentService.deleteCommList(id);
            return "ok";
        }else
            return "fail";
    }

    /*
     * Comment（评论）操作
     * */

    // 保存Comment对象
    @PostMapping("/client/comm/save")
    @ResponseBody
    public String saveObj(Comment comment,HttpSession session){
        comment.setCrateTime(LocalDateTime.now(ZoneId.of("Asia/Shanghai"))); // 或 .of("GMT+8") 或 .now()默认时区
        User user=(User) session.getAttribute("ANEC_USER_SESSION");
        comment.setUserId(user.getUserId());
        return commentService.saveObj(comment)!=null ? "ok":"fail";
    }

    // 增加Comment点赞数量1次
    @GetMapping("/comm/praInc/{commId}")
    @ResponseBody
    public String praiseIncre(@PathVariable("commId") String commentId){
        return commentService.praiseIncre(commentId);
    }

    // 减少Comment点赞数量1次
    @GetMapping("/comm/praDec/{commId}")
    @ResponseBody
    public String praiseDecre(@PathVariable("commId") String commentId){
        return commentService.praiseDecre(commentId);
    }

    // 根据id删除Comment
    @GetMapping("/admin/comm/delete/{commId}")
    @ResponseBody
    public String deleteComm(@PathVariable("commId") String commId){
        return commentService.deleteComm(commId);
    }
}
