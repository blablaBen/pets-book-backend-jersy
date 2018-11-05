package com.app.server.models;

import java.util.Date;

public class ChatMessage {
    private String id = null;
    private String chatRoomId;
    private String userId;
    private String message;
    private Date time;

    public ChatMessage(String chatRoomId, String userId, String message, Date time) {
        this.chatRoomId = chatRoomId;
        this.userId = userId;
        this.message = message;
        this.time = time;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}
