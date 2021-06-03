package models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.Image;

public class Piece {

    @JsonProperty("_id")
    public String id;
    private Image image;

    public Piece(String id){
        this.id = id;
    }
}
