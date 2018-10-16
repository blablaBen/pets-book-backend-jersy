package com.app.server.models;

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
    String time;
    String userId;
    String username;


    public PostComment(String postId, String content, String time, String userId) {
        this.postId = postId;
        this.content = content;
        this.time = time;
        this.userId = userId;
    }

    public void setId(String id) {
        this.id = id;
    }
}
