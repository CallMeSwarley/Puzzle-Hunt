package com.socialgaming.androidtutorial.Models;

import android.graphics.Bitmap;

public class PieceViewItem {

    private Bitmap image;
    private int amount;

    private String setId;
    private int horizontalPosition;
    private int verticalPosition;

    public PieceViewItem(Bitmap image) {
        this.amount = -1;
        this.image = image;
    }

    public PieceViewItem(Bitmap image, int amount) {
        this.image = image;
        this.amount = amount;
    }

    public PieceViewItem(Bitmap image, String setId, int horizontalPosition, int verticalPosition) {
        this.image = image;
        this.amount = -1;
        this.setId = setId;
        this.horizontalPosition = horizontalPosition;
        this.verticalPosition = verticalPosition;
    }

    public PieceViewItem(Bitmap image, int amount, String setId, int horizontalPosition, int verticalPosition) {
        this.image = image;
        this.amount = amount;
        this.setId = setId;
        this.horizontalPosition = horizontalPosition;
        this.verticalPosition = verticalPosition;
    }

    public Bitmap getImage() {
        return image;
    }

    public int getAmount() {
        return amount;
    }

    public String getSetId() {
        return setId;
    }

    public int getHorizontalPosition() {
        return horizontalPosition;
    }

    public int getVerticalPosition() {
        return verticalPosition;
    }
}
