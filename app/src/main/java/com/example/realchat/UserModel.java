package com.example.realchat;

public class UserModel {
    String Uid,phoneNumber,name;

    public UserModel(String uid, String name, String phoneNumber) {

        Uid = uid;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    public String getUid() {
        return Uid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }
}
