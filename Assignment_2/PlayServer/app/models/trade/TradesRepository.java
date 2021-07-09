package models.trade;

import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.ArrayList;
import java.util.List;

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
        Trade result = trades().findOne("{traderId:#}", firebaseId).as(Trade.class);
        if (result == null)
            result = trades().findOne("{partnerId:#}", firebaseId).as(Trade.class);
        return result;
    }

    public List<Trade> getOpenTrades(String firebaseId) {
        MongoCursor<Trade> trades = trades().find().as(Trade.class);
        List<Trade> result = new ArrayList();
        for (Trade t : trades) {
            result.add(t);
        }
        return result;
    }

    public Trade getTradeById(String tradeId) {
        Trade result = trades().findOne("{_id:#}", tradeId).as(Trade.class);
        return result;
    }

    public void insert(Trade trade) {
        trades().save(trade);
    }

    public void update(Trade trade) {
        trades().update("{_id:#}", trade.id).with(copy(trade));
    }

    public void delete(String tradeId) {
        trades().remove("{_id:#}", tradeId);
    }

    public Trade copy(Trade trade) {
        Trade copy = new Trade();
        copy.partnerTradeItems = trade.partnerTradeItems;
        copy.id = trade.id;
        copy.partnerOffer = trade.partnerOffer;
        copy.partnerId = trade.partnerId;
        copy.traderId = trade.traderId;
        copy.traderOffer = trade.traderOffer;
        copy.traderTradeItems = trade.traderTradeItems;
        copy.traderAccepted = trade.traderAccepted;
        copy.partnerAccepted = trade.partnerAccepted;
        return copy;
    }

    public Trade getTradeWithBothIds(String firebaseId, String partnerId) {
        Trade result = trades().findOne("{traderId:#,partnerId:#}", firebaseId, partnerId).as(Trade.class);
        if (result == null)
            result = trades().findOne("{partnerId:#,traderId:#}", firebaseId, partnerId).as(Trade.class);
        return result;
    }
}
