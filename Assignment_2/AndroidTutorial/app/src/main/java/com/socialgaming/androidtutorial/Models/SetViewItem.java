package com.socialgaming.androidtutorial.Models;

import android.media.Image;
import android.view.View;

import java.util.Comparator;

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

    public static Comparator<SetViewItem> AlphabeticalComparator = new Comparator<SetViewItem>() {
        @Override
        public int compare(SetViewItem o1, SetViewItem o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    public static Comparator<SetViewItem> MostPiecesComparator = new Comparator<SetViewItem>() {
        @Override
        public int compare(SetViewItem o1, SetViewItem o2) {
            return o2.getOwnedPieces() - o1.getOwnedPieces();
        }
    };

    public static Comparator<SetViewItem> LeastPiecesComparator = new Comparator<SetViewItem>() {
        @Override
        public int compare(SetViewItem o1, SetViewItem o2) {
            return o1.getOwnedPieces() - o2.getOwnedPieces();
        }
    };

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
