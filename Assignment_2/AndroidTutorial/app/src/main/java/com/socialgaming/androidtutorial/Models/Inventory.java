package com.socialgaming.androidtutorial.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {

    private String id;
    private List<String> titles;
    private Map<String, int[][]> sets;

    public Inventory() {
        this.id = "";
        this.titles = new ArrayList();
        this.sets = new HashMap<>();
    }

    public Inventory(String firebaseId) {
        this.id = firebaseId;
        this.titles = new ArrayList();
        this.sets = new HashMap<>();
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    public void setSets(Map<String, int[][]> sets) {
        this.sets = sets;
    }

    public String getId() {
        return id;
    }

    public List<String> getTitles() {
        return titles;
    }

    public Map<String, int[][]> getSets() {
        return sets;
    }
}
