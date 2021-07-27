package com.example.realchat.User;

import java.io.Serializable;

public class UserObject implements Serializable {
    private String uid,name,phone;
    private Boolean selected = false;
    UserObject(String Uid,String name,String phone){
        this.name=name;
        this.phone=phone;
        this.uid = Uid;
    }

    public String getname(){ return name;}
    public String getphone() {return phone;}
    public String getUid() {return uid;}
    public void setName(String name) {this.name =name;}

    public Boolean getSelected() {
        return selected;
    }
}
