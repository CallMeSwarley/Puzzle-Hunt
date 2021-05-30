package com.socialgaming.androidtutorial.Models;

import android.graphics.Bitmap;

public class Puzzle {
    private Bitmap image;
    private int piecesCountHorizontal=1;
    private int piecesCountVertical=1;
    private PuzzlePiece[][] puzzlePieces;
    public String id;

    public Puzzle(String id, int piecesCountHorizontal, int piecesCountVertical){
        this.id = id;
        this.piecesCountHorizontal = piecesCountHorizontal;
        this.piecesCountVertical = piecesCountVertical;
        this.puzzlePieces = new PuzzlePiece[piecesCountHorizontal][piecesCountVertical];
    }


    public PuzzlePiece getPuzzlePiece(int positionHorizontal, int positionVertical){
        if(puzzlePieces[positionHorizontal][positionVertical]==null) {
            Bitmap bmp;
            int k = 0;
            int width = image.getWidth();
            int height = image.getHeight();
            bmp = Bitmap.createBitmap(image, (width * positionHorizontal) / piecesCountHorizontal, (positionVertical * height) / positionVertical, width / positionHorizontal, height / piecesCountVertical);
            PuzzlePiece ret = new PuzzlePiece(bmp, positionHorizontal, positionVertical, this);
            puzzlePieces[positionHorizontal][positionVertical] = ret;
        }
        return puzzlePieces[positionHorizontal][positionVertical];
    }

}
