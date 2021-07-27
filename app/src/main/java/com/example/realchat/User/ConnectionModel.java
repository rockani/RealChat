package com.example.realchat.User;

public class ConnectionModel {
    String uesrID,PhoneNum;

    public ConnectionModel(String uesrID, String phoneNum) {
        this.uesrID = uesrID;
        PhoneNum = phoneNum;
    }

    public String getUesrID() {
        return uesrID;
    }

    public String getPhoneNum() {
        return PhoneNum;
    }

    public void setUesrID(String uesrID) {
        this.uesrID = uesrID;
    }

    public void setPhoneNum(String phoneNum) {
        PhoneNum = phoneNum;
    }
}
