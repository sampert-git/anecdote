package com.springboot.anecdote.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 轶事（Anecdote）实体类
 *
 * @author Sampert
 * @version 1.0
 * @date 2020/9/11 18:47
 */
public class Anecdote implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 轶事id */
    private int anecId;
    /** 主人公 */
    private String anecPerson;
    /** 标题 */
    private String anecTitle;
    /** 内容 */
    private String anecContent;
    /** 创建时间 */
    private LocalDateTime anecCreateTime;
    /** 创建人id */
    private int anecCreateId;
    /** 图片路径（只存文件名） */
    private String anecImgPath;

    /** 创建人名称（非数据库字段） */
    private String anecCreateName;
    /** 评论列表（非数据库字段） */
    private List<Comment> comments;
    /** 创建时间字符串形式（非数据库字段） */
    private String anecTimeStr;

    public String getAnecTimeStr() {
        return anecTimeStr;
    }

    public void setAnecTimeStr(String anecTimeStr) {
        this.anecTimeStr = anecTimeStr;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getAnecCreateName() {
        return anecCreateName;
    }

    public void setAnecCreateName(String anecCreateName) {
        this.anecCreateName = anecCreateName;
    }

    public String getAnecImgPath() {
        return anecImgPath;
    }

    public void setAnecImgPath(String anecImgPath) {
        this.anecImgPath = anecImgPath;
    }

    public int getAnecId() {
        return anecId;
    }

    public void setAnecId(int anecId) {
        this.anecId = anecId;
    }

    public String getAnecPerson() {
        return anecPerson;
    }

    public void setAnecPerson(String anecPerson) {
        this.anecPerson = anecPerson;
    }

    public String getAnecTitle() {
        return anecTitle;
    }

    public void setAnecTitle(String anecTitle) {
        this.anecTitle = anecTitle;
    }

    public String getAnecContent() {
        return anecContent;
    }

    public void setAnecContent(String anecContent) {
        this.anecContent = anecContent;
    }

    public LocalDateTime getAnecCreateTime() {
        return anecCreateTime;
    }

    public void setAnecCreateTime(LocalDateTime anecCreateTime) {
        this.anecCreateTime = anecCreateTime;
    }

    public int getAnecCreateId() {
        return anecCreateId;
    }

    public void setAnecCreateId(int anecCreateId) {
        this.anecCreateId = anecCreateId;
    }

    @Override
    public String toString() {
        return "Anecdote{" +
                "anecId=" + anecId +
                ", anecPerson='" + anecPerson + '\'' +
                ", anecTitle='" + anecTitle + '\'' +
                ", anecContent='" + anecContent + '\'' +
                ", anecCreateTime=" + anecCreateTime +
                ", anecCreateId=" + anecCreateId +
                ", anecCreateName='" + anecCreateName + '\'' +
                ", anecImgPath='" + anecImgPath + '\'' +
                '}';
    }
}
