package com.rula.rendas.obj;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Message {
    public String message;
    public String senderid;
    public String time;

    public Message(){}

    public Message(String message, String senderid, String time) {
        this.message = message;
        this.senderid = senderid;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderid() {
        return senderid;
    }

    public String getTime() {
        return time;
    }
}
