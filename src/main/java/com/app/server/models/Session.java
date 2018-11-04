package com.app.server.models;


import com.app.server.util.APPCrypt;

public class Session {

    String token = null;
    String userId = null;
    String profileName = null;

    public Session(User user) throws Exception {
        this.userId = user.getId();
        this.token = APPCrypt.encrypt(userId);
        this.profileName = user.getProfileName();

    }
}
