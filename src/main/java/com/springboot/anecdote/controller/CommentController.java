package com.springboot.anecdote.controller;

import com.springboot.anecdote.entity.Comment;
import com.springboot.anecdote.entity.User;
import com.springboot.anecdote.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 评论（Comment）控制器
 *
 * @author Sampert
 * @version 1.0
 * @date 2021/5/16
 */
@Controller
public class CommentController {

    private CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 保存Comment对象
    @PostMapping("/client/comm/save")
    @ResponseBody
    public String saveComment(Comment comment, HttpSession session) {
        // 或 .of("GMT+8") 或 .now()默认时区（MongoDB存储时间字段会转化为UTC，方便查看起见这里加8小时，取出时再减去）
        comment.setCrateTime(LocalDateTime.now(ZoneId.of("Asia/Shanghai")).plusHours(8));
        User user = (User) session.getAttribute("ANEC_USER_SESSION");
        comment.setUserId(user.getUserId());
        return commentService.saveObj(comment) != null ? "ok" : "fail";
    }

    // 增加Comment点赞数量1次
    @GetMapping("/comm/praInc/{commId}")
    @ResponseBody
    public String praiseIncre(@PathVariable("commId") String commentId) {
        return commentService.praiseIncre(commentId);
    }

    // 减少Comment点赞数量1次
    @GetMapping("/comm/praDec/{commId}")
    @ResponseBody
    public String praiseDecre(@PathVariable("commId") String commentId) {
        return commentService.praiseDecre(commentId);
    }

    // 根据id删除Comment
    @GetMapping("/admin/comm/delete/{commId}")
    @ResponseBody
    public String deleteComm(@PathVariable("commId") String commId) {
        return commentService.deleteComm(commId);
    }
}
