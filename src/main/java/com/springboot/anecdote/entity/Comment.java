package com.springboot.anecdote.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论（Comment）实体类，评论数据存储于 MongoDB
 *
 * @author Sampert
 * @version 1.0
 * @date 2020/9/19 13:35
 */
@Document(collection = "comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 评论id */
    @Id
    private String id;
    /** 评论发表人id */
    private Integer userId;
    /** 发表时间 */
    private LocalDateTime crateTime;
    /** 评论所属Anecdote的id */
    private Integer anecId;
    /** 评论内容 */
    private String content;
    /** 点赞数量 */
    private Integer praise;

    /** 评论发表人姓名（非数据库字段）*/
    private String userName;
    /** 发表时间字符串表示（非数据库字段）*/
    private String timeStr;

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAnecId() {
        return anecId;
    }

    public void setAnecId(Integer anecId) {
        this.anecId = anecId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getCrateTime() {
        return crateTime;
    }

    public void setCrateTime(LocalDateTime crateTime) {
        this.crateTime = crateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPraise() {
        return praise;
    }

    public void setPraise(Integer praise) {
        this.praise = praise;
    }
}
