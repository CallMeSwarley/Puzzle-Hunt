package models.trade;

import org.jongo.MongoCollection;

import javax.inject.Inject;

import uk.co.panaxiom.playjongo.PlayJongo;

public class TradesRepository {
    @Inject
    PlayJongo jongo;
    private static TradesRepository instance = null;

    public TradesRepository() {
        instance = this;
    }

    public static TradesRepository getInstance() {
        return instance;
    }

    public MongoCollection trades() {
        return jongo.getCollection("trades");
    }

    public Trade getTrade(String firebaseId) {
        Trade result = trades().findOne("{playerOne:#}", firebaseId).as(Trade.class);
        if (result == null)
            result = trades().findOne("{playerTwo:#}", firebaseId).as(Trade.class);
        return result;
    }

    public void insert(Trade trade) {
        trades().save(trade);
    }

}
