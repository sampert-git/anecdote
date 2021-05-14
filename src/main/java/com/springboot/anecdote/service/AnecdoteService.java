package com.springboot.anecdote.service;

import com.springboot.anecdote.entity.Anecdote;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AnecdoteService {
    // 查找Anecdotes列表
    List<Anecdote> listAnecdotes(String keyword, Integer pageNum);

    // 根据ID查找Anecdote
    Anecdote getAnecdoteById(Integer id);

    // 根据创建人id查找Anecdotes列表
    List<Anecdote> listAnecdotesByCreUser(Integer createId);

    // 添加Anecdote
    int saveAnecdote(Anecdote anecdote, MultipartFile file);

    // 更新Anecdote
    int updateAnecdote(Anecdote anecdote, Integer pageNum);

    // 删除Anecdote
    int deleteAnecdote(Integer id, Integer creUserId);

    // 根据path删除Anecdote对应图片
    boolean deleteAnecImg(String anecImgPath);
}
