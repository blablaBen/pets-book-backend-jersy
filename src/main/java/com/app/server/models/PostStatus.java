package com.app.server.models;

import java.util.Date;
import java.util.List;

public class PostStatus {
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    String id=null;
    String userId;
    String textValue;
    List<String> pictures;
    Date date;
    int commentCount;


    public PostStatus(String userId, String textValue, List<String> pictures, Date date) {
        this.userId = userId;
        this.textValue = textValue;
        this.pictures = pictures;
        this.date = date;
    }

    public void setId(String id) {
        this.id = id;
    }
}
