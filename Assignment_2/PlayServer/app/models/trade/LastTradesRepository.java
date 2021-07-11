package models.trade;

import org.jongo.MongoCollection;

import java.time.LocalDate;

import javax.inject.Inject;

import uk.co.panaxiom.playjongo.PlayJongo;

public class LastTradesRepository {
    @Inject
    PlayJongo jongo;
    private static LastTradesRepository instance = null;

    public LastTradesRepository() {
        instance = this;
    }

    public static LastTradesRepository getInstance() {
        return instance;
    }


    public MongoCollection lastTrades() {
        return jongo.getCollection("lastTrades");
    }

    public LastTrade getLastTrade(String firebaseId, String partnerId) {
        LastTrade result = lastTrades().findOne("{playerOne:#,playerTwo:#}", firebaseId, partnerId).as(LastTrade.class);
        if (result == null)
            result = lastTrades().findOne("{playerOne:#,playerTwo:#}", partnerId, firebaseId).as(LastTrade.class);
        return result;
    }

    public LastTrade getLastTrade(String tradeId) {
        return lastTrades().findOne("{_id:#}", tradeId).as(LastTrade.class);
    }

    public void insert(Trade trade) {
        LastTrade lastTrade = new LastTrade();
        lastTrade.id = trade.id;
        lastTrade.playerOne = trade.traderId;
        lastTrade.playerTwo = trade.partnerId;
        lastTrade.playerAccepted = trade.traderOffer;
        lastTrade.traderAccepted = trade.partnerOffer;
        LocalDate now = LocalDate.now();
        lastTrade.dayOfYear = now.getDayOfYear();
        lastTrade.year = now.getYear();
        lastTrades().save(lastTrade);
    }

    public void update(LastTrade lastTrade) {
        lastTrades().update("{_id:#}", lastTrade.id).with(copy(lastTrade));
    }

    public LastTrade copy(LastTrade lastTrade) {
        LastTrade copy = new LastTrade();
        copy.id = lastTrade.id;
        copy.playerOne = lastTrade.playerOne;
        copy.playerTwo = lastTrade.playerTwo;
        copy.dayOfYear = lastTrade.dayOfYear;
        copy.year = lastTrade.year;
        copy.playerTwo = lastTrade.playerTwo;
        copy.playerOne = lastTrade.playerOne;
        copy.traderAccepted = lastTrade.traderAccepted;
        copy.playerAccepted = lastTrade.playerAccepted;
        return copy;
    }

    public void log(Trade trade) {
        LastTrade lastTrade = this.getLastTrade(trade.traderId, trade.partnerId);
        if (lastTrade == null) {
            insert(trade);
        } else {
            LocalDate now = LocalDate.now();
            lastTrade.playerOne = trade.traderId;
            lastTrade.playerTwo = trade.partnerId;
            lastTrade.year = now.getYear();
            lastTrade.dayOfYear = now.getDayOfYear();
            lastTrade.playerAccepted = trade.traderOffer;
            lastTrade.traderAccepted = trade.partnerOffer;
            update(lastTrade);
        }
    }
}
