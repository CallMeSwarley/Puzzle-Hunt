package com.socialgaming.androidtutorial.Models;

import java.util.HashMap;
import java.util.Map;

public class Trade {

    public String id;
    public String traderId;
    public String partnerId;
    public Map<String, int[][]> traderTradeItems;
    public Map<String, int[][]> partnerTradeItems;
    public boolean oneAccepted;
    public boolean twoAccepted;
    public Offer traderAccepted;
    public Offer partnerAccepted;

    public Trade() {
        this.id = "";
        this.traderTradeItems = new HashMap<>();
        this.partnerTradeItems = new HashMap<>();
    }

    public Trade(String firebaseId) {
        this.id = firebaseId;
        this.traderTradeItems = new HashMap<>();
        this.partnerTradeItems = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public Map<String, int[][]> getTraderTradeItems() {
        return traderTradeItems;
    }

    public Map<String, int[][]> getPartnerTradeItems() {
        return partnerTradeItems;
    }
}
