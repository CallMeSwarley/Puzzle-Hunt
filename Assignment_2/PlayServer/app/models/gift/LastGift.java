package models.gift;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LastGift {
    @JsonProperty("_id")
    public String id;
    public int dayOfYear;
    public int year;
}
