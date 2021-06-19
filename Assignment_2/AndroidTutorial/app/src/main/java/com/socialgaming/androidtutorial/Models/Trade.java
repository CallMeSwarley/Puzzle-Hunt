package com.socialgaming.androidtutorial.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Trade {

    public String id;
    public Map<String, int[][]> playerOneTradeItems;
    public Map<String, int[][]> playerTwoTradeItems;

    public Trade() {
        this.id = "";
        this.playerOneTradeItems = new HashMap<>();
        this.playerTwoTradeItems = new HashMap<>();
    }

    public Trade(String firebaseId) {
        this.id = firebaseId;
        this.playerOneTradeItems = new HashMap<>();
        this.playerTwoTradeItems = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public Map<String, int[][]> getPlayerOneTradeItems() {
        return playerOneTradeItems;
    }

    public Map<String, int[][]> getPlayerTwoTradeItems() {
        return playerTwoTradeItems;
    }
}
