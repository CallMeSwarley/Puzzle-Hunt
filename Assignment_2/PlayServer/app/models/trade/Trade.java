package models.trade;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class Trade {
    @JsonProperty("_id")
    public String id;
    public String traderId;
    public String partnerId;
    public Map<String, int[][]> traderTradeItems;
    public Map<String, int[][]> partnerTradeItems;
    public boolean traderAccepted;
    public boolean partnerAccepted;
    public Offer traderOffer;
    public Offer partnerOffer;

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
