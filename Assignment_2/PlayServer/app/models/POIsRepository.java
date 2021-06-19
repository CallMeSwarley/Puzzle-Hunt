package models;

import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import uk.co.panaxiom.playjongo.PlayJongo;
import utils.Utilities;

@Singleton
public class POIsRepository {
    @Inject
    private PlayJongo jongo;

    private static POIsRepository instance = null;

    public POIsRepository() {
        instance = this;
    }

    public static POIsRepository getInstance() {
        return instance;
    }

    public MongoCollection pois() {
        MongoCollection poiCollection = jongo.getCollection("pois");
        return poiCollection;
    }

    public void insert(POI poi) {
        pois().save(poi);
    }

    public POI getPOI(String id) {
        return pois().findOne("{_id: #}", id).as(POI.class);
    }

    public POI[] getNearbyPOIs(Double lat, Double lon) {
        MongoCursor<POI> pois = pois().find().as(POI.class);
        List<POI> result = new ArrayList();
        for (POI saved : pois) {
            if (Utilities.distance(lat, lon, saved.lat, saved.lon) <= Utilities.POIVisibilityRange) {
                result.add(saved);
            }
        }
        return result.toArray(new POI[0]);
    }

}
