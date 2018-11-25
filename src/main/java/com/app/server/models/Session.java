package com.app.server.models;


import com.app.server.util.CheckAuthentication;

public class Session {

    String token = null;
    String userId = null;
    String profileName = null;

    public Session(User user) throws Exception {
        this.userId = user.getId();
        this.token = CheckAuthentication.generateToken(userId);
        this.profileName = user.getProfileName();

    }
}
