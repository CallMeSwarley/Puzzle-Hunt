package com.socialgaming.androidtutorial.Models;

import java.util.Map;

public class Gift {
    public String id;
    public String friendshipID;
    public String receiverID;
    public Map<String, int[][]> content;
    public int dayOfYear;
    public int year;

    public Gift() {

    }
}
