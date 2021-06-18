package models;

import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import uk.co.panaxiom.playjongo.PlayJongo;
import utils.Utilities;

public class ShopsRepository {
    @Inject
    private PlayJongo jongo;

    private static ShopsRepository instance = null;

    public ShopsRepository() {
        instance = this;
    }

    public static ShopsRepository getInstance() {
        return instance;
    }

    public MongoCollection shops() {
        MongoCollection shopCollection = jongo.getCollection("shops");
        return shopCollection;
    }

    public void insert(Shop shop) {
        shops().save(shop);
    }

    public Shop getShop(String id) {
        return shops().findOne("{_id: #}", id).as(Shop.class);
    }

    public Shop[] getAllShops() {
        MongoCursor<Shop> othersQuery = shops().find().as(Shop.class);
        List<Shop> shopList = new ArrayList<>();
        for (Shop shop : othersQuery)
            shopList.add(shop);
        return  shopList.toArray(new Shop[0]);
    }
    public Shop[] getVisible(Double latSW, Double lonSW, Double latNE, Double lonNE) {
        MongoCursor<Shop> othersQuery = shops().find().as(Shop.class);
        List<Shop> dealerList = new ArrayList<>();
        for (Shop shop : othersQuery)
            dealerList.add(shop);
        return dealerList
                .stream()
                .filter(s -> Utilities.inBound(latSW, lonSW, latNE, lonNE, s.lat, s.lon))
                .toArray(Shop[]::new);
    }

}
