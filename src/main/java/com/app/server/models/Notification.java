package com.app.server.models;

import java.util.Date;

public class Notification {
    private String id = null;
    private String userId;
    private String type;
    private String content;
    private Date date;
    private boolean isRead;

    public Notification(String userId, String type, String content, boolean isRead, Date date) {
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.isRead = isRead;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
