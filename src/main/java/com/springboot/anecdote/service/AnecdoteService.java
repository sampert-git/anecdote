package com.springboot.anecdote.service;

import com.springboot.anecdote.entity.Anecdote;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AnecdoteService {
    // 查找Anecdotes列表
    List<Anecdote> findListAnecdotes(String keyword,Integer pageNum);
    // 根据ID查找Anecdote
    Anecdote findAnecdoteById(Integer id);
    // 根据创建人id查找Anecdotes
    List<Anecdote> findAnecsByCreUser(Integer createId);
    // 添加Anecdote
    int addAnecdote(Anecdote anecdote, MultipartFile file);
    // 更新Anecdote
    int updateAnecdote(Anecdote anecdote,Integer pageNum);
    // 删除Anecdote
    int deleteAnecdote(Integer id,Integer creUserId);
}
