package com.example.realchat.Chat;

public class MessageModel {
    String message;
    String time;
    String from;


    public MessageModel(String message,String from, String time ) {
        this.message = message;
        this.time = time;
        this.from = from;

    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getFrom() {
        return from;
    }



    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setFrom(String from) {
        this.from = from;
    }


}
