package models;

import org.jongo.MongoCollection;

import javax.inject.Inject;
import javax.inject.Singleton;

import uk.co.panaxiom.playjongo.PlayJongo;

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
        MongoCollection locationCollection = jongo.getCollection("locations");
        return locationCollection;
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
}
