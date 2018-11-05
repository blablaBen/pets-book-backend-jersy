package com.app.server.models;

public class Notification {
    private String id = null;
    private String userId;
    private String type;
    private String content;
    private boolean isRead;

    public Notification(String userId, String type, String content, boolean isRead) {
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.isRead = isRead;
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
}
