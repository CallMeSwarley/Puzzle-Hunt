package models;

import java.awt.Image;

public class Puzzle {
    private byte[] image;
    public int piecesCountHorizontal=1;
    public int piecesCountVertical=1;
    public String id;

    public Puzzle(String id, int piecesCountHorizontal, int piecesCountVertical,byte[] image){
        this.id = id;
        this.piecesCountHorizontal = piecesCountHorizontal;
        this.piecesCountVertical = piecesCountVertical;
        this.image = image;
    }

    public Puzzle(int piecesCountHorizontal, int piecesCountVertical,byte[] image){
        this.piecesCountHorizontal = piecesCountHorizontal;
        this.piecesCountVertical = piecesCountVertical;
        this.image = image;
    }

    public Puzzle(String id, int piecesCountHorizontal, int piecesCountVertical){
        this.piecesCountHorizontal = piecesCountHorizontal;
        this.piecesCountVertical = piecesCountVertical;
    }




}
