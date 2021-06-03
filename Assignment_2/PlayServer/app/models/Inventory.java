package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Inventory {

    @JsonProperty("_id")
    String id;

    public Inventory(String id){
        this.id = id;
    }

}
