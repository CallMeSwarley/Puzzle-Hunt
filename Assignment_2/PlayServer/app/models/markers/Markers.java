package models.markers;

import java.util.Map;

import models.Dealer;
import models.Shop;

public class Markers {
    public Dealer[] activeDealers = new Dealer[0];
    public Dealer[] visibleDealers = new Dealer[0];
    public Shop[] activeShops = new Shop[0];
    public Shop[] visibleShops = new Shop[0];
    public Map<String, Double[]> nearbyUsers;
}
