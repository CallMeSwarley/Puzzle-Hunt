package com.socialgaming.androidtutorial.Models;

public class TradeItem {

    private int listPosition;
    private String setId;
    private int horizontalPosition;
    private int verticalPosition;

    public TradeItem(int listPosition, String setId, int horizontalPosition, int verticalPosition) {
        this.listPosition = listPosition;
        this.setId = setId;
        this.horizontalPosition = horizontalPosition;
        this.verticalPosition = verticalPosition;
    }

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public int getHorizontalPosition() {
        return horizontalPosition;
    }

    public void setHorizontalPosition(int horizontalPosition) {
        this.horizontalPosition = horizontalPosition;
    }

    public int getVerticalPosition() {
        return verticalPosition;
    }

    public void setVerticalPosition(int verticalPosition) {
        this.verticalPosition = verticalPosition;
    }
}
