package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Puzzle {
    @JsonProperty("_id")
    public String id;
    public int piecesCountHorizontal = 1;
    public int piecesCountVertical = 1;


    public Puzzle() {
    }


    public Puzzle(String id, int piecesCountHorizontal, int piecesCountVertical) {
        this.id = id;
        this.piecesCountHorizontal = piecesCountHorizontal;
        this.piecesCountVertical = piecesCountVertical;
    }
}
