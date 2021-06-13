package com.socialgaming.androidtutorial.Models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class PuzzlePiece {
    //private Puzzle parent;
    //image is a piece of the whole puzzle
    private Puzzle puzzleParent;
    private Bitmap image;
    //the position of the piece in an 2d array [horizontal][vertical]
    private int positionHorizontal;
    private int positionVertical;

    public PuzzlePiece(Bitmap image, int positionHorizontal, int positionVertical, Puzzle puzzleParent){
        this.image = image;
        this.positionHorizontal = positionHorizontal;
        this.positionVertical = positionVertical;
        this.puzzleParent = puzzleParent;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getPositionHorizontal() {
        return positionHorizontal;
    }

    public void setPositionHorizontal(int positionHorizontal) {
        this.positionHorizontal = positionHorizontal;
    }

    public int getPositionVertical() {
        return positionVertical;
    }

    public void setPositionVertical(int positionVertical) {
        this.positionVertical = positionVertical;
    }
    public  Puzzle getPuzzleParent(){
        return puzzleParent;
    }

}
