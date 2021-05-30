package models;

import java.awt.Image;

public class Puzzle {
    private Image image;
    public int piecesCountHorizontal=1;
    public int piecesCountVertical=1;
    public String id;

    public Puzzle(String id, int piecesCountHorizontal, int piecesCountVertical){
        this.id = id;
        this.piecesCountHorizontal = piecesCountHorizontal;
        this.piecesCountVertical = piecesCountVertical;
    }




}
