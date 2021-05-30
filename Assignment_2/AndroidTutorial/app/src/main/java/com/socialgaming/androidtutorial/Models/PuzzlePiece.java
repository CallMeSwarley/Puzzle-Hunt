package com.socialgaming.androidtutorial.Models;

import android.graphics.Bitmap;
import android.graphics.Color;

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

    public Bitmap getGrayImage(){
        Bitmap grayImage = Bitmap.createBitmap(image.getWidth(),image.getHeight(),image.getConfig());
        int A,R,G,B;
        int colorPixel;
        int width = image.getWidth();
        int height = image.getHeight();

        for(int x=0; x < height; x++){
            for(int y=0; y < width; y++){
                colorPixel = image.getPixel(x,y);
                A = Color.alpha(colorPixel);
                R = Color.red(colorPixel);
                G = Color.green(colorPixel);
                B = Color.blue(colorPixel);

                R = (R+G+B)/3;
                G = R;
                B = R;

                grayImage.setPixel(x,y, Color.argb(A,R,G,B));

            }
        }
        return grayImage;
    }


}
