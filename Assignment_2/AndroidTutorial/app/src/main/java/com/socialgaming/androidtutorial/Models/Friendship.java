package com.socialgaming.androidtutorial.Models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Friendship {

    public String id; //MongoID
    public String friendOne;
    public String friendTwo;
    public int rank; //je nach Rank anderen Multiplier beim traden etc. 0->4
    public int dayOfYear,year;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Friendship(String du,String freund, String ID){
        id=ID;
        friendOne=du;
        friendTwo=freund;
        rank=0;//Default startwert, soll immer dann aktualisiert werden wenn freundesliste geöffnet wird
        LocalDate friendshipStart=java.time.LocalDate.now();
        dayOfYear=friendshipStart.getDayOfYear();
        year=friendshipStart.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateRank(){
        LocalDate today=java.time.LocalDate.now();
        long daysOfFriendship= ChronoUnit.DAYS.between(LocalDate.ofYearDay(year,dayOfYear),today);
        if(daysOfFriendship<=2)
            rank=0;
        if (2<daysOfFriendship&&daysOfFriendship<=4)
            rank=1;
        if (4<daysOfFriendship&&daysOfFriendship<=6)
            rank=2;
        if (6<daysOfFriendship&&daysOfFriendship<=8)
            rank=3;
        if (8<daysOfFriendship)
            rank=4;
    }
}
