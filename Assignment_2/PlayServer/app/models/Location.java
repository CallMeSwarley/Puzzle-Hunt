package models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;

public class Location {

    @JsonProperty("_id")
    public String id;
    public Double[] loc1;
    public Double[] loc2;
    public Double[] loc3;
    public Integer hour;
    public Integer minute;
    public Integer second;
    public Integer nano;

    public Location() {
    }

    public Location(String id, Double[] loc) {
        this.id = id;
        this.loc1 = loc;
        setTime();
    }

    public void updateLocation(Double[] loc) {
        this.loc3 = loc2;
        this.loc2 = loc1;
        this.loc1 = loc;
        setTime();
    }

    public void setTime() {
        LocalTime now = LocalTime.now();
        this.hour = now.getHour();
        this.minute=now.getMinute();
        this.second=now.getSecond();
        this.nano=now.getNano();
    }
}
