package com.app.server.models;

import java.util.Date;

public class ForgetPassword {

    private String id;
    private String email;
    private String tokenKey;
    private Date expiredTime;

    public ForgetPassword() {
    }

    public ForgetPassword(String email, String tokenKey, Date expiredTime) {
        this.email = email;
        this.tokenKey = tokenKey;
        this.expiredTime = expiredTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }
}
