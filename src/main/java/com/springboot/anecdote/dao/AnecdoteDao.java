package com.springboot.anecdote.dao;

import com.springboot.anecdote.entity.Anecdote;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnecdoteDao {
    List<Anecdote> findListAnecdotes(String keyword);       // 查找Anecdotes列表

    Anecdote findAnecdoteById(Integer id);                  // 根据ID查找Anecdote

    List<Anecdote> findAnecsByCreUser(Integer createId);    // 根据创建人id查找Anecdotes

    int addAnecdote(Anecdote anecdote);                     // 添加Anecdote

    int updateAnecdote(Anecdote anecdote);                  // 更新Anecdote

    int deleteAnecdote(Integer id);                         // 根据ID删除Anecdote
}
