package models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class User {
    @JsonProperty("_id")
    public String id;
    public String nickName;
    public Long xp;
    public List<String> friends;//Key sind die MongoIds
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

    public void addFriend(String friendshipID) {
        if (!friends.contains(friendshipID)) {
            friends.add(friendshipID);
        }
    }

    public void removeFriend(String firebaseId) {
        friends.remove(this.id + firebaseId);
    }

    public void updateNickname(String nickName) {
        this.nickName = nickName;
    }
}
