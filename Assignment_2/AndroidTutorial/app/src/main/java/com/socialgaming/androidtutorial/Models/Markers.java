package com.socialgaming.androidtutorial.Models;

import java.util.HashMap;
import java.util.Map;

public class Markers {
    public Dealer[] activeDealers = new Dealer[0];
    public Dealer[] visibleDealers = new Dealer[0];
    public Shop[] activeShops = new Shop[0];
    public Shop[] visibleShops = new Shop[0];
    public Map<String, User> nearbyUsers = new HashMap();
    public Map<String, Double[]> userLocations = new HashMap();

}
