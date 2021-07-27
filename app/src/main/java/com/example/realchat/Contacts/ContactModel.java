package com.example.realchat.Contacts;

public class ContactModel {

    public String name;
    String Uid;
    public String mobileNumber;
    private Boolean selected = false;
    public String getUid() {
        return Uid;
    }

    public ContactModel(String Uid, String name, String mobileNumber) {
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.Uid = Uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
    public Boolean getSelected() {
        return selected;
    }

}
