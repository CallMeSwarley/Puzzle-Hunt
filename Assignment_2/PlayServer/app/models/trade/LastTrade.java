package models.trade;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LastTrade {
    @JsonProperty("_id")
    public String id;
    public String playerOne;
    public String playerTwo;
    public int dayOfYear;
    public int year;
    public Offer playerAccepted;
    public Offer traderAccepted;
}
