package com.app.server.models;

import java.util.Date;

public class PostComment {
    public String getId() {
        return id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    String id=null;
    String postId;
    String content;
    Date date;
    String userId;
    String username;


    public PostComment(String postId, String content, Date time, String userId) {
        this.postId = postId;
        this.content = content;
        this.date = time;
        this.userId = userId;
    }

    public void setId(String id) {
        this.id = id;
    }
}
