package com.springboot.anecdote.service.impl;

import com.springboot.anecdote.dao.AnecdoteDao;
import com.springboot.anecdote.entity.Anecdote;
import com.springboot.anecdote.service.AnecdoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Class AnecdoteServiceImpl 
 * Description //TODO AnecdoteService实现类；
 * Date 2020/9/12 9:19
 * @author Sampert
 * @version 1.0
 **/
@CacheConfig(cacheNames = "anecdoteCache")
@Service
public class AnecdoteServiceImpl implements AnecdoteService {

    private AnecdoteDao anecdoteDao;
    private final String pathPrefix = "upload/imgs/anec_imgs/";
    private SimpleDateFormat dateFormat;

    @Autowired
    public AnecdoteServiceImpl(AnecdoteDao anecdoteDao) {
        this.anecdoteDao = anecdoteDao;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    // 查找Anecdotes列表
    @Cacheable(value = "anecdoteCacheList",key = "'anecList_'+#pageNum",condition = "#keyword==null")
    @Override
    public List<Anecdote> findListAnecdotes(String keyword,Integer pageNum) {
        List<Anecdote> anecdotes = anecdoteDao.findListAnecdotes(keyword);
        for (Anecdote anecdote : anecdotes) {
            anecdote.setAnecImgPath(pathPrefix + anecdote.getAnecImgPath());
            anecdote.setAnecTimeStr(dateFormat.format(anecdote.getAnecCreateTime()));
        }
        return anecdotes;
    }

    // 根据id查找Anecdote
    @Cacheable(key="'anec_'+#id")
    @Override
    public Anecdote findAnecdoteById(Integer id) {
        Anecdote anecdote = anecdoteDao.findAnecdoteById(id);
        anecdote.setAnecImgPath(pathPrefix + anecdote.getAnecImgPath());
        anecdote.setAnecTimeStr(dateFormat.format(anecdote.getAnecCreateTime()));
        return anecdote;
    }

    // 根据创建人id查找Anecdotes列表
    @Cacheable(key = "'anecCre_'+#createId")
    @Override
    public List<Anecdote> findAnecsByCreUser(Integer createId) {
        List<Anecdote> anecdotes = anecdoteDao.findAnecsByCreUser(createId);
        for (Anecdote anecdote : anecdotes) {
            anecdote.setAnecImgPath(pathPrefix + anecdote.getAnecImgPath());
            anecdote.setAnecTimeStr(dateFormat.format(anecdote.getAnecCreateTime()));
        }
        return anecdotes;
    }

    // 添加Anecdote（事务管理隔离级别isolation和传播行为propagation均为默认值）
    @Transactional(isolation = Isolation.DEFAULT,propagation = Propagation.REQUIRED)
    @Caching(evict = {@CacheEvict(value = "anecdoteCacheList",allEntries = true), @CacheEvict(key = "'anecCre_'+#anecdote.anecCreateId")})
    @Override
    public int addAnecdote(Anecdote anecdote, MultipartFile file) {
        if (file!=null){
            String originalName=file.getOriginalFilename();
            if(originalName != null){
                String[] suffixArray = {".jpg",".jpeg",".png"};
                String suffix = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
                if (!Arrays.asList(suffixArray).contains(suffix)) {
                    return -1;
                }
                // UUID重命名文件
                String newName= UUID.randomUUID()+suffix;
                // 图片上传绝对路径
                String path = "E:/anecdote-upload/imgs/anec_imgs";
                try {
                    Path targetDir= Paths.get(path);
                    if(!Files.exists(targetDir)){
                        Files.createDirectories(targetDir);
                    }
                    Path targetFile=targetDir.resolve(newName);
                    Files.write(targetFile,file.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                anecdote.setAnecImgPath(newName);
            }
        }
        return anecdoteDao.addAnecdote(anecdote);
    }

    // 更新Anecdote
    @Transactional
    @Caching(evict = {@CacheEvict(value = "anecdoteCacheList", key = "'anecList_'+#pageNum"),
            @CacheEvict(key = "'anecCre_'+#anecdote.anecCreateId"), @CacheEvict(key = "'anec_'+#anecdote.anecId")})
    @Override
    public int updateAnecdote(Anecdote anecdote,Integer pageNum) {
        return anecdoteDao.updateAnecdote(anecdote);
    }

    // 根据ID删除Anecdote
    @Transactional
    @Caching(evict = {@CacheEvict(value = "anecdoteCacheList",allEntries = true),
            @CacheEvict(key = "'anec_'+#id"),@CacheEvict(key = "'anecCre_'+#creUserId")})
    @Override
    public int deleteAnecdote(Integer id,Integer creUserId) {
        return anecdoteDao.deleteAnecdote(id);
    }
}
