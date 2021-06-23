package com.socialgaming.androidtutorial.Models;

import java.util.ArrayList;
import java.util.List;

public class User implements Comparable{
    public String id;
    public String nickName;
    public Long xp;
    public List<String> friends;
    public String description;

    public User() {
        this.id = "";
        this.nickName = "";
        this.xp = 0L;
        this.friends = new ArrayList<>();
        this.description = "";
    }

    public User(String firebaseId) {
        this();
        this.id = firebaseId;
        this.nickName = firebaseId;
    }

    public User(String firebaseId, String nickName) {
        this(firebaseId);
        this.nickName = nickName;
    }
//TODO update add/rm fun
    public void addFriend(String firebaseId) {
        if (!friends.contains(firebaseId))
            friends.add(firebaseId);
    }

    public void removeFriend(String firebaseId) {
        friends.remove(firebaseId);
    }

    public void updateNickname(String nickName) {
        this.nickName = nickName;
    }

    public String getName() {
        return this.nickName;
    }

    public String getXP() {
        return this.xp.toString();
    }

    public void setXP(String xp) {
        this.xp = Long.parseLong(xp);
    }

    public void setNickName(String name) {
        this.nickName = name;
    }



    @Override
    public int compareTo(Object o) {
        int compareXp = Integer.parseInt(((User)o).getXP());
        return compareXp-Integer.parseInt(this.xp.toString());
    }
}

