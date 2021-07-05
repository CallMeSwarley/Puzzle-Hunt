package models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class Inventory {

    @JsonProperty("_id")
    public String id;
    public Map<String, String> titles;
    //public List<String> titles;
    public Map<String, int[][]> sets;

    public Inventory() {
        this.id = "";
        this.titles = new HashMap<>();
        //this.titles = new ArrayList();
        this.sets = new HashMap<>();
    }

    public Inventory(String firebaseId) {
        this.id = firebaseId;
        this.titles = new HashMap<>();
        //this.titles = new ArrayList();
        this.sets = new HashMap<>();
    }

    public void setTitles(Map<String, String> titles) {
        this.titles = titles;
    }

    public void setSets(Map<String, int[][]> sets) {
        this.sets = sets;
    }

    public String getId() {
        return id;
    }

    public Map<String, String> getTitles() {
        return titles;
    }

    public Map<String, int[][]> getSets() {
        return sets;
    }

    public void cleanUp() {
        Map<String, int[][]> afterCleanUp = new HashMap<>(this.sets);
        for (Map.Entry<String, int[][]> entry : sets.entrySet()) {
            boolean allEmpty = true;
            for (int[] row : entry.getValue()) {
                for (int v : row)
                    allEmpty = allEmpty && v <= 0;
            }
            if (allEmpty)
                afterCleanUp.remove(entry.getKey());
        }
    }
}
