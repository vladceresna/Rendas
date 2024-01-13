package com.rula.rendas.obj;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;

@IgnoreExtraProperties
public class Channel {
    public String name;
    public ArrayList<String> ownerid;
    public ArrayList<String> membersid;
    public HashMap<String, Message> messages;

    public Channel(){

    }

    public Channel(String name, ArrayList<String> ownerid, ArrayList<String> membersid, HashMap<String, Message> messages) {
        this.name = name;
        this.ownerid = ownerid;
        this.membersid = membersid;
        this.messages = messages;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getOwnerid() {
        return ownerid;
    }

    public ArrayList<String> getMembersid() {
        return membersid;
    }

    public HashMap<String, Message> getMessages() {
        return messages;
    }
}
