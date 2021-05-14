package com.springboot.anecdote.service.impl;

import com.springboot.anecdote.dao.AnecdoteDao;
import com.springboot.anecdote.entity.Anecdote;
import com.springboot.anecdote.service.AnecdoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Class AnecdoteServiceImpl
 * Description AnecdoteService实现类；
 * Date 2020/9/12 9:19
 *
 * @author Sampert
 * @version 1.0
 **/
@CacheConfig(cacheNames = "cacheAnecdote")
@Service
public class AnecdoteServiceImpl implements AnecdoteService {

    private AnecdoteDao anecdoteDao;
    private static final String PATH_PREFIX = "upload/";
    private static final String CACHE_ANEC_LIST_NAME = "cacheAnecdoteList";
    private static final String CACHE_ANEC_LIST_PREFIX = "'anecList_'";
    private static final String CACHE_ANEC_PREFIX = "'anec_'";
    private static final String CACHE_ANEC_CRE_PREFIX = "'anecCre_'";
    @Value("${file.location.upload}")
    private String fileUploadLocation;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public AnecdoteServiceImpl(AnecdoteDao anecdoteDao) {
        this.anecdoteDao = anecdoteDao;
    }

    // 查找Anecdotes列表
    @Cacheable(value = CACHE_ANEC_LIST_NAME, key = CACHE_ANEC_LIST_PREFIX + " + #pageNum", condition = "#keyword == null")
    @Override
    public List<Anecdote> listAnecdotes(String keyword, Integer pageNum) {
        List<Anecdote> anecdotes = anecdoteDao.findListAnecdotes(keyword);
        for (Anecdote anecdote : anecdotes) {
            anecdote.setAnecImgPath(PATH_PREFIX + anecdote.getAnecImgPath());
            anecdote.setAnecTimeStr(anecdote.getAnecCreateTime().format(formatter));
        }
        return anecdotes;
    }

    // 根据id查找Anecdote
    @Cacheable(key = CACHE_ANEC_PREFIX + " + #id")
    @Override
    public Anecdote getAnecdoteById(Integer id) {
        Anecdote anecdote = anecdoteDao.findAnecdoteById(id);
        anecdote.setAnecImgPath(PATH_PREFIX + anecdote.getAnecImgPath());
        anecdote.setAnecTimeStr(anecdote.getAnecCreateTime().format(formatter));
        return anecdote;
    }

    // 根据创建人id查找Anecdotes列表
    @Cacheable(key = CACHE_ANEC_CRE_PREFIX + " + #createId")
    @Override
    public List<Anecdote> listAnecdotesByCreUser(Integer createId) {
        List<Anecdote> anecdotes = anecdoteDao.findAnecsByCreUser(createId);
        for (Anecdote anecdote : anecdotes) {
            anecdote.setAnecImgPath(PATH_PREFIX + anecdote.getAnecImgPath());
            anecdote.setAnecTimeStr(anecdote.getAnecCreateTime().format(formatter));
        }
        return anecdotes;
    }

    // 添加Anecdote（事务管理隔离级别isolation和传播行为propagation均为默认值）
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    @Caching(evict = {@CacheEvict(value = CACHE_ANEC_LIST_NAME, allEntries = true),
            @CacheEvict(key = CACHE_ANEC_CRE_PREFIX + " + #anecdote.anecCreateId")})
    @Override
    public int saveAnecdote(Anecdote anecdote, MultipartFile file) {
        if (file != null) {
            String originalName = file.getOriginalFilename();
            if (originalName != null) {
                String[] suffixArray = {".jpg", ".jpeg", ".png"};
                String suffix = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
                if (!Arrays.asList(suffixArray).contains(suffix)) {
                    return -1;
                }
                // UUID重命名文件
                String newName = UUID.randomUUID() + suffix;
                // 图片上传保存路径（从 file:E:/anecdote-upload/imgs/anec_imgs/ 第6个字符（下标为5）开始）
                String path = fileUploadLocation.substring(5);
                try {
                    Path targetDir = Paths.get(path);
                    if (!Files.exists(targetDir)) {
                        Files.createDirectories(targetDir);
                    }
                    Path targetFile = targetDir.resolve(newName);
                    Files.write(targetFile, file.getBytes());
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
    @Caching(evict = {@CacheEvict(value = CACHE_ANEC_LIST_NAME, key = CACHE_ANEC_LIST_PREFIX + " + #pageNum"),
            @CacheEvict(key = CACHE_ANEC_CRE_PREFIX + " + #anecdote.anecCreateId"),
            @CacheEvict(key = CACHE_ANEC_PREFIX + " + #anecdote.anecId")})
    @Override
    public int updateAnecdote(Anecdote anecdote, Integer pageNum) {
        return anecdoteDao.updateAnecdote(anecdote);
    }

    // 根据ID删除Anecdote
    @Transactional
    @Caching(evict = {@CacheEvict(value = CACHE_ANEC_LIST_NAME, allEntries = true),
            @CacheEvict(key = CACHE_ANEC_PREFIX + " + #id"), @CacheEvict(key = CACHE_ANEC_CRE_PREFIX + " + #creUserId")})
    @Override
    public int deleteAnecdote(Integer id, Integer creUserId) {
        return anecdoteDao.deleteAnecdote(id);
    }

    // 根据path删除Anecdote对应图片
    @Override
    public boolean deleteAnecImg(String anecImgPath) {
        // 去掉“file:”前缀
        String path = fileUploadLocation.substring(5);
        try {
            Path targetDir = Paths.get(path);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            Path targetFile = targetDir.resolve(anecImgPath);
            return Files.deleteIfExists(targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
