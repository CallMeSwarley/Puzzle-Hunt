package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class POI {
    @JsonProperty("_id")
    String id;
    String name;
    Double lat;
    Double lon;
    Double range;

    public POI() {
    }


}
