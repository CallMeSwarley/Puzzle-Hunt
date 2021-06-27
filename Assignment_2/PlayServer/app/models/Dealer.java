package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Dealer {
    @JsonProperty("_id")
    public String id;
    public String title;
    public Double lat;
    public Double lon;
    public Long range;
    public Long despawnAt;

    public Dealer() {
    }
}
