package com.app.server.models;

public class PetProfile {
    private String id = null;
    private String ownerUserId;
    private String description;
    private String portraitUrl;

    public PetProfile(String ownerUserId, String description, String portraitUrl) {
        this.ownerUserId = ownerUserId;
        this.description = description;
        this.portraitUrl = portraitUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public void setPortraitUrl(String portraitUrl) {
        this.portraitUrl = portraitUrl;
    }


}
