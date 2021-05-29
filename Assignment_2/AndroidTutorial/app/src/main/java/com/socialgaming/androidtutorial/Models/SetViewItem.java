package com.socialgaming.androidtutorial.Models;

import android.media.Image;
import android.view.View;

public class SetViewItem {

    private String name;
    private View image;
    private int ownedPieces;
    private int maxPieces;

    public SetViewItem(String name, int ownedPieces, int maxPieces) {
        this.name = name;
        this.ownedPieces = ownedPieces;
        this.maxPieces = maxPieces;
    }

    public SetViewItem(String name, View image, int ownedPieces, int maxPieces) {
        this.name = name;
        this.image = image;
        this.ownedPieces = ownedPieces;
        this.maxPieces = maxPieces;
    }

    public String getName() {
        return name;
    }

    public View getImage() {
        return image;
    }

    public int getOwnedPieces() {
        return ownedPieces;
    }

    public int getMaxPieces() {
        return maxPieces;
    }
}
