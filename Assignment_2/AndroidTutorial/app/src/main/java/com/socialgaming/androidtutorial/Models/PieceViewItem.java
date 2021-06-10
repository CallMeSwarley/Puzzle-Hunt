package com.socialgaming.androidtutorial.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

public class PieceViewItem {

    private Bitmap image;
    private int amount;

    public PieceViewItem(Bitmap image) {
        this.amount = -1;
        this.image = image;
    }

    public PieceViewItem(Bitmap image, int amount) {

        this.image = image;
        this.amount = amount;

    }

    public Bitmap getImage() {
        return image;
    }

    public int getAmount() {
        return amount;
    }
}
