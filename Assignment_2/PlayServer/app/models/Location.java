package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Location {

    @JsonProperty("_id")
    public String id;
    public Double[] loc1;
    public Double[] loc2;
    public Double[] loc3;

    public Location() {
    }

    public Location(String id, Double[] loc) {
        this.id = id;
        this.loc1 = loc;
    }

    public void updateLocation(Double[] loc) {
        this.loc3 = loc2;
        this.loc2 = loc1;
        this.loc1 = loc;
    }

}
