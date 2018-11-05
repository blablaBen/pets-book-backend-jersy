package com.app.server.models;

public class ChatRoom {
    private String id = null;
    private String petOwnerUserId;
    private String vetUserId;

    public ChatRoom(String petOwnerUserId, String vetUserId) {
        this.petOwnerUserId = petOwnerUserId;
        this.vetUserId = vetUserId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPetOwnerUserId() {
        return petOwnerUserId;
    }

    public void setPetOwnerUserId(String petOwnerUserId) {
        this.petOwnerUserId = petOwnerUserId;
    }

    public String getVetUserId() {
        return vetUserId;
    }

    public void setVetUserId(String vetUserId) {
        this.vetUserId = vetUserId;
    }
}
