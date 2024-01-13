package com.rula.rendas.obj;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class Chat {
    public String name;
    public ArrayList<String> ownerid;
    public ArrayList<String> membersid;
    public ArrayList<Channel> channels;

    public Chat(){}

    public Chat(String name, ArrayList<String> ownerid, ArrayList<String> membersid, ArrayList<Channel> channels) {
        this.name = name;
        this.ownerid = ownerid;
        this.membersid = membersid;
        this.channels = channels;
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

    public ArrayList<Channel> getChannels() {
        return channels;
    }
}
