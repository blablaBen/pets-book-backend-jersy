package com.app.server.models;


public class User {


    private String id = null;
    private String key;
    private String email;
    private String password;
    private String profileName;
    private Integer userType;
    private Integer userLevel = 1;
    private Integer userScore = 10;
    private String portraitUrl;

    public boolean isSSOUser() {
        return isSSOUser;
    }

    public void setSSOUser(boolean SSOUser) {
        isSSOUser = SSOUser;
    }

    private boolean isSSOUser;

    public User(String key, String email, String password, String profileName, Integer userType, String portraitUrl) {
        this.key = key;
        this.email = email;
        this.password = password;
        this.profileName = profileName;
        this.userType = userType;
        this.portraitUrl = portraitUrl;
        this.isSSOUser = false;
    }

    public User(String key, String email, String password, String profileName, Integer userType, int userLevel, int userScore, String portraitUrl) {
        this.key = key;
        this.email = email;
        this.password = password;
        this.profileName = profileName;
        this.userLevel = userLevel;
        this.userScore = userScore;
        this.userType = userType;
        this.portraitUrl = portraitUrl;
        this.isSSOUser = false;
    }

    public User() {
    }

    public static void main(String[] args) {
        User user = new User("qwertu43323", "test@hotmail.com", "asdgf2345", "test", 1, "http://xxx.com/1234.jpg");

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Integer getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }

    public Integer getUserScore() {
        return userScore;
    }

    public void setUserScore(Integer userScore) {
        this.userScore = userScore;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public void setPortraitUrl(String portraitUrl) {
        this.portraitUrl = portraitUrl;
    }
}
