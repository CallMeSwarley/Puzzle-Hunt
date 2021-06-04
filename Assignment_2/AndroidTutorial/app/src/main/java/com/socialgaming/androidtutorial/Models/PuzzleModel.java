package com.socialgaming.androidtutorial.Models;

public class PuzzleModel {
    public String id;
    public int piecesCountHorizontal = 1;
    public int piecesCountVertical = 1;


    public PuzzleModel() {
    }


    public PuzzleModel(String id, int piecesCountHorizontal, int piecesCountVertical) {
        this.id = id;
        this.piecesCountHorizontal = piecesCountHorizontal;
        this.piecesCountVertical = piecesCountVertical;
    }
}
