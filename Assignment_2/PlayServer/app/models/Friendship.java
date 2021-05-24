package models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class Friendship {
    @JsonProperty("_id")
    public String id; //DeineID+FreundID
    public String friendOne;
    public String friendTwo;
    public FriendshipRank rank; //je nach Rank anderen Multiplier beim traden etc.
    public LocalDate friendshipStart;

    public Friendship(String du,String freund){
        id=du+freund;
        friendOne=du;
        friendTwo=freund;
        rank=FriendshipRank.FRIENDLY_GREETINGS;//Default startwert, soll immer dann aktualisiert werden wenn freundesliste geöffnet wird
        friendshipStart=java.time.LocalDate.now();
    }

    public void updateRank(){
        LocalDate today=java.time.LocalDate.now();
        long daysOfFriendship= ChronoUnit.DAYS.between(friendshipStart,today);
        //TODO Tage-Rang Verhältnis anpassen
        if(daysOfFriendship<=2)
            rank=FriendshipRank.FRIENDLY_GREETINGS;
        if (2<daysOfFriendship&&daysOfFriendship<=4)
            rank=FriendshipRank.CO_PUZZLERS;
        if (4<daysOfFriendship&&daysOfFriendship<=6)
            rank=FriendshipRank.PUZZLE_BUDDIES;
        if (6<daysOfFriendship&&daysOfFriendship<=8)
            rank=FriendshipRank.PUZZLE_BFF;
        if (8<daysOfFriendship)
            rank=FriendshipRank.PUZZLE_SOULMATES;
    }
}
