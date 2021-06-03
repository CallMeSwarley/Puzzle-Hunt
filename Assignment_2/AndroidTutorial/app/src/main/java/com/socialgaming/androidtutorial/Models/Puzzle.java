package com.socialgaming.androidtutorial.Models;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;

import java.util.Collections;
import java.util.List;

public class Puzzle {
    private Bitmap image;
    public int piecesCountHorizontal=1;
    public int piecesCountVertical=1;
    private PuzzlePiece[][] puzzlePieces;
    public String id;

    public Puzzle(String id, int piecesCountHorizontal, int piecesCountVertical){
        this.id = id;
        this.piecesCountHorizontal = piecesCountHorizontal;
        this.piecesCountVertical = piecesCountVertical;
        this.puzzlePieces = new PuzzlePiece[piecesCountHorizontal][piecesCountVertical];
    }

    public Puzzle(String id, int piecesCountHorizontal, int piecesCountVertical, Bitmap image){
        this.id = id;
        this.piecesCountHorizontal = piecesCountHorizontal;
        this.piecesCountVertical = piecesCountVertical;
        this.puzzlePieces = new PuzzlePiece[piecesCountHorizontal][piecesCountVertical];
        this.image = image;
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

    public PuzzlePiece[][] getAllPuzzlePieces(){
        for(int i=0; i<piecesCountHorizontal; i++){
            for(int j=0; j<piecesCountVertical; j++){
                if(puzzlePieces[i][j]==null) {
                    Bitmap bmp;
                    int width = image.getWidth();
                    int height = image.getHeight();
                    bmp=Bitmap.createBitmap(image,(width*j)/piecesCountHorizontal,(i*height)/piecesCountVertical,width/piecesCountHorizontal,height/piecesCountVertical);
                    PuzzlePiece ret = new PuzzlePiece(bmp, i, j, this);
                    puzzlePieces[i][j] = ret;
                }
            }
        }
        return this.puzzlePieces;
    }





}
