package models;


import org.jongo.Find;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import uk.co.panaxiom.playjongo.PlayJongo;
import utils.Utilities;

@Singleton
public class LocationsRepository {
    @Inject
    private PlayJongo jongo;

    private static LocationsRepository instance = null;

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
        copy.hour = loc.hour;
        copy.minute = loc.minute;
        copy.second = loc.second;
        copy.nano = loc.nano;
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
                .filter(loc -> Utilities.distance(userLocation, loc) < Utilities.DISTANCE_THRESHOLD)
                .map(loc -> loc.id).toArray(String[]::new);
    }

    public List<Double[]> getAllLastLocations() {
        MongoCursor<Location> othersQuery = locations().find().as(Location.class);
        List<Double[]> resultList = new ArrayList();
        for (Location loc : othersQuery) {
            resultList.add(loc.loc1);
        }
        return resultList;
    }

    public void convertToNew() {
        Find othersQuery = locations().find();


    }

}
