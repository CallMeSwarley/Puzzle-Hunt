package models.gift;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Gift {
    @JsonProperty("_id")
    public String id;
    public String friendshipID;
    public String receiverID;
    public Map<String, int[][]> content;

    public Gift() {

    }
}

