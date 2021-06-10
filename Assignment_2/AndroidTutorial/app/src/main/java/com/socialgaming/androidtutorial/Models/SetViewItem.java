package com.socialgaming.androidtutorial.Models;

import java.util.Comparator;

public class SetViewItem {

    private String title;
    private int imageId;
    private int ownedPieces;
    private int maxPieces;

    public SetViewItem(String title, int ownedPieces, int maxPieces) {
        this.title = title;
        this.ownedPieces = ownedPieces;
        this.maxPieces = maxPieces;
    }

    public SetViewItem(String title, int imageId, int ownedPieces, int maxPieces) {
        this.title = title;
        this.ownedPieces = ownedPieces;
        this.maxPieces = maxPieces;
        this.imageId = imageId;
    }

    public static Comparator<SetViewItem> AlphabeticalComparator = new Comparator<SetViewItem>() {
        @Override
        public int compare(SetViewItem o1, SetViewItem o2) {
            return o1.getTitle().compareTo(o2.getTitle());
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

    public String getTitle() {
        return title;
    }

    public int getImage() {
        return imageId;
    }

    public int getOwnedPieces() {
        return ownedPieces;
    }

    public int getMaxPieces() {
        return maxPieces;
    }
}
