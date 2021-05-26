package models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class User {
    @JsonProperty("_id")
    public String id;
    public String nickName;
    public Long xp;
    public HashMap<String, Friendship> friends;//Key sind die kombinierten ids der Freunde
    public String description;

    public User() {
        this.id = "";
        this.nickName = "";
        this.xp = 0L;
        this.friends = new HashMap<>();
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

    public void addFriend(String firebaseId) {
        String friendshipId = this.id + firebaseId;
        if (!friends.containsKey(friendshipId)) {
            Friendship fs = new Friendship(this.id, firebaseId);
            friends.put(friendshipId, fs);
        }
    }

    public void removeFriend(String firebaseId) {
        friends.remove(this.id + firebaseId);
    }

    public void updateNickname(String nickName) {
        this.nickName = nickName;
    }
}
