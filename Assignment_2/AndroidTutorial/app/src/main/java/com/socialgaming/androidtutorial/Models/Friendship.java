package com.socialgaming.androidtutorial.Models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Friendship {
    enum FriendshipRank {
        // Je nach Länge der Freundschaft ändert sich der Ran
        FRIENDLY_GREETINGS, CO_PUZZLERS, PUZZLE_BUDDIES, PUZZLE_BFF, PUZZLE_SOULMATES
    }
    public String id; //MongoID
    public String friendOne;
    public String friendTwo;
    public FriendshipRank rank; //je nach Rank anderen Multiplier beim traden etc.
    public int dayOfYear,year;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Friendship(String du, String freund){
        friendOne=du;
        friendTwo=freund;
        rank=FriendshipRank.FRIENDLY_GREETINGS;//Default startwert, soll immer dann aktualisiert werden wenn freundesliste geöffnet wird
        LocalDate friendshipStart=java.time.LocalDate.now();
        dayOfYear=friendshipStart.getDayOfYear();
        year=friendshipStart.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateRank(){
        LocalDate today=java.time.LocalDate.now();
        long daysOfFriendship= ChronoUnit.DAYS.between(LocalDate.ofYearDay(year,dayOfYear),today);
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
