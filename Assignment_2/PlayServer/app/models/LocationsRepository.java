package models;


import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import uk.co.panaxiom.playjongo.PlayJongo;

@Singleton
public class LocationsRepository {
    @Inject
    private PlayJongo jongo;

    private static LocationsRepository instance = null;
    private static final double DISTANCE_THRESHOLD = 10;

    public LocationsRepository() {
        instance = this;
    }

    public static LocationsRepository getInstance() {
        return instance;
    }

    public MongoCollection locations() {
        return jongo.getCollection("locations");
    }

    public Location getLocation(String id) {
        return locations().findOne("{_id: #}", id).as(Location.class);
    }

    public void insert(Location loc) {
        locations().save(loc);
    }

    public void update(Location loc) {
        locations().update("{_id: #}", loc.id).with(copyLocation(loc));
    }

    public Location copyLocation(Location loc) {
        Location copy = new Location();
        copy.id = loc.id;
        copy.loc1 = loc.loc1;
        copy.loc2 = loc.loc2;
        copy.loc3 = loc.loc3;
        return copy;
    }

    public String[] getNearbyUsers(String firebaseId) {
        Location userLocation = this.getLocation(firebaseId);
        MongoCursor<Location> othersQuery = locations().find().as(Location.class);
        List<Location> locationList = new ArrayList<>();
        for (Location loc : othersQuery)
            locationList.add(loc);
        return locationList.stream()
                .filter(x -> x != null && !x.id.equals(firebaseId))
                .filter(loc -> distance(userLocation, loc) < LocationsRepository.DISTANCE_THRESHOLD)
                .map(loc -> loc.id).toArray(String[]::new);
    }


    //Source: https://www.geeksforgeeks.org/program-distance-two-points-earth/
    public static double distance(Location loc1, Location loc2) {
        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        double lon1 = Math.toRadians(loc1.loc1[1]);
        double lon2 = Math.toRadians(loc2.loc1[1]);
        double lat1 = Math.toRadians(loc1.loc1[0]);
        double lat2 = Math.toRadians(loc2.loc1[0]);
        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));
        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371e3;
        // calculate the result
        return (c * r);
    }
}
