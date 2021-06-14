package models;

import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import uk.co.panaxiom.playjongo.PlayJongo;
import utils.Utilities;

public class DealersRepository {
    @Inject
    private PlayJongo jongo;

    private static DealersRepository instance = null;

    public DealersRepository() {
        instance = this;
    }

    public static DealersRepository getInstance() {
        return instance;
    }

    public MongoCollection dealers() {
        return jongo.getCollection("dealers");
    }

    public void insert(Dealer dealer) {
        dealers().save(dealer);
    }

    public Dealer getDealer(String id) {
        return dealers().findOne("{_id: #}", id).as(Dealer.class);
    }

    public Dealer[] getVisible(Double latSW, Double lonSW, Double latNE, Double lonNE) {
        MongoCursor<Dealer> othersQuery = dealers().find().as(Dealer.class);
        List<Dealer> dealerList = new ArrayList<>();
        for (Dealer dealer : othersQuery)
            dealerList.add(dealer);
        return dealerList
                .stream()
                .filter(d -> Utilities.inBound(latSW, lonSW, latNE, lonNE, d.lat, d.lon))
                .toArray(Dealer[]::new);
    }
}
