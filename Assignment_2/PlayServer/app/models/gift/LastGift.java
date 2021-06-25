package models.gift;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LastGift {
    @JsonProperty("_id")
    public String id;
    public String friendship;
    public String receiverId;
    public int dayOfYear;
    public int year;
}
