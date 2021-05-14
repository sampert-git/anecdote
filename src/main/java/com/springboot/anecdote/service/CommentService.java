package com.springboot.anecdote.service;

import com.springboot.anecdote.entity.Comment;

import java.util.List;

public interface CommentService {
    // 保存Comment对象
    Comment saveObj(Comment comment);

    // 增加Comment点赞数量1次
    String praiseIncre(String commentId);

    // 减少Comment点赞数量1次
    String praiseDecre(String commentId);

    // 根据anecId查找Comment列表
    List<Comment> findCommsByAnecId(Integer anecId);

    // 根据id删除Comment
    String deleteComm(String commId);

    // 根据anecId删除Comment列表
    void deleteCommList(Integer anecId);
}
