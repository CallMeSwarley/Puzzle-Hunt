package models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class User {
    @JsonProperty("_id")
    public String id;
    public String nickName;
    public Long xp;
    public List<String> friends;

    public User() {
    }

    public User(String firebaseId) {
        this.id = firebaseId;
        this.nickName = firebaseId;
        this.xp = 0L;
        this.friends = new ArrayList<>();
    }

    public User(String firebaseId, String nickName) {
        this.id = firebaseId;
        this.nickName = nickName;
        this.xp = 0L;
        this.friends = new ArrayList<>();
    }

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
}
