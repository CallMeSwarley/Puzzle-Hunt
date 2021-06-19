package models.trade;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class Trade {
    @JsonProperty("_id")
    public String id;
    public String playerOne;
    public String playerTwo;

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
