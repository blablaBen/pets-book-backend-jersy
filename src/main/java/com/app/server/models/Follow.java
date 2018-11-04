package com.app.server.models;

import java.util.ArrayList;

public class Follow {


    private String id = null;
    private String userId;
    private ArrayList<String> following = new ArrayList<>();
    private ArrayList<String> followed = new ArrayList<>();

    public Follow(String userId, ArrayList<String> following, ArrayList<String> followed) {
        this.userId = userId;
        this.following = following;
        this.followed = followed;
    }

    public Follow() {
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

    public ArrayList<String> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<String> following) {
        this.following = following;
    }

    public ArrayList<String> getFollowed() {
        return followed;
    }

    public void setFollowed(ArrayList<String> followed) {
        this.followed = followed;
    }
}
