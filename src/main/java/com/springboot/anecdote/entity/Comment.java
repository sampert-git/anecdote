package com.springboot.anecdote.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Class Comment 
 * Description //TODO Comment(评论)实体类
 * Date 2020/9/19 13:35
 * @author Sampert
 * @version 1.0
 **/
@Document(collection = "comment")
public class Comment implements Serializable {
    private static final long serialVersionUID=1L;

    @Id
    private String id;          // 唯一标识id
    private Integer userId;     // 评论发表人id
    private String userName;    // 发表人姓名
    private LocalDateTime crateTime;     // 发表时间
    private Integer anecId;     // 评论所属Anecdote的id
    private String content;     // 评论内容
    private Integer praise;     // 点赞数量
    private String timeStr;     // 发表时间字符串表示；

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
