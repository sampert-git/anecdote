package com.springboot.anecdote.service.impl;

import com.springboot.anecdote.entity.Comment;
import com.springboot.anecdote.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Class CommentServiceImpl
 * Description CommentService实现类
 * Date 2020/10/8 16:52
 *
 * @author Sampert
 * @version 1.0
 */
@Service
public class CommentServiceImpl implements CommentService {
    private MongoTemplate mongoTemplate;

    @Autowired
    public CommentServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // 保存Comment对象
    @Override
    public Comment saveObj(Comment comment) {
        return mongoTemplate.save(comment);
    }

    // 增加Comment点赞数量1次
    @Override
    public String praiseIncre(String commentId) {
        Query query = new Query(Criteria.where("_id").is(commentId));
        Update update = new Update().inc("praise", 1);
        mongoTemplate.updateFirst(query, update, Comment.class);      // 更新Query结果集第一条
        // mongoTemplate.updateMulti(query, update, Comment.class);   // 更新Query结果集全部
        // mongoTemplate.upsert(query, update, Comment.class);        // 无匹配时插入新值
        return "ok";
    }

    // 减少Comment点赞数量1次
    @Override
    public String praiseDecre(String commentId) {
        Query query = new Query(Criteria.where("_id").is(commentId));
        Update update = new Update().inc("praise", -1);
        mongoTemplate.updateFirst(query, update, Comment.class);
        return "ok";
    }

    // 根据anecId查找Comment列表
    @Override
    public List<Comment> findCommsByAnecId(Integer anecId) {
        Query query = new Query(Criteria.where("anecId").is(anecId));
        query.with(Sort.by(Sort.Direction.DESC, "_id"));
        return mongoTemplate.find(query, Comment.class);
    }

    // 根据id删除Comment
    @Override
    public String deleteComm(String commId) {
        Query query = new Query(Criteria.where("_id").is(commId));
        Comment comment = mongoTemplate.findOne(query, Comment.class);
        if (comment != null) {
            mongoTemplate.remove(comment);
            return "ok";
        }
        return "fail";
    }

    // 根据anecId删除评论列表（当该anecId对应Anecdote被删除时调用）
    @Override
    public void deleteCommList(Integer anecId) {
        Query query = new Query(Criteria.where("anecId").is(anecId));
        mongoTemplate.findAllAndRemove(query, Comment.class, "comment");
    }
}
