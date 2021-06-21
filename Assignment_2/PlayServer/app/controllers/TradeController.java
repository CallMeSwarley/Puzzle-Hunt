package controllers;


import com.google.gson.Gson;

import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.HashMap;

import javax.inject.Inject;

import models.InventoryRepository;
import models.trade.LastTrade;
import models.trade.LastTradesRepository;
import models.trade.Offer;
import models.trade.Offers;
import models.trade.Trade;
import models.trade.TradesRepository;
import play.mvc.Controller;
import play.mvc.Result;

public class TradeController extends Controller {
    private final Gson gson = new Gson();
    @Inject
    private TradesRepository trades;
    @Inject
    private LastTradesRepository lastTrades;
    @Inject
    private InventoryRepository inventories;

    public Result getOpenTrade(String firebaseId) {
        Trade openTrade = trades.getTrade(firebaseId);
        return ok(gson.toJson(openTrade));
    }

    /**
     * @return "Fail" wenn ein Trade nicht möglich ist
     * Trade-Objekt wenn der Trade begonnen hat, egal ob neu oder erneut
     */
    public Result beginTrade(String firebaseId, String partnerId) {
        LocalDate now = LocalDate.now();
        LastTrade lastTrade = lastTrades.getLastTrade(firebaseId, partnerId);
        Trade trade = new Trade();
        if (lastTrade != null) {
            if ((lastTrade.year < now.getYear() || lastTrade.dayOfYear != now.getDayOfYear())) {
                trade.id = lastTrade.id;
                trade.playerOne = lastTrade.playerOne;
                trade.playerTwo = lastTrade.playerTwo;
            } else {
                return ok("FAIL");
            }
        } else {
            trade.id = new ObjectId().toString();
            trade.playerOne = firebaseId;
            trade.playerTwo = partnerId;
        }
        trade.playerOneTradeItems = new HashMap<>();
        trade.playerTwoTradeItems = new HashMap<>();
        trade.oneAccepted = false;
        trade.twoAccepted = false;
        trade.playerOneAccepted = new Offer();
        trade.playerTwoAccepted = new Offer();
        trades.insert(trade);
        return ok(gson.toJson(trade));
    }

    public Result trade(String tradeId, String firebaseId, String offer) {
        return ok();
    }

    public Result offer(String tradeId, String firebaseId, String offers) {
        Trade open = trades.getTrade(tradeId);
        if (open.playerOne.equals(firebaseId)) {
            open.playerOneTradeItems = gson.fromJson(offers, Offers.class);
        } else {
            open.playerTwoTradeItems = gson.fromJson(offers, Offers.class);
        }
        trades.update(open);
        return ok(gson.toJson(open));
    }

    public Result accept(String tradeId, String firebaseId, String offer) {
        Trade open = trades.getTrade(tradeId);
        if (open.playerOne.equals(firebaseId)) {
            open.oneAccepted = true;
            open.playerOneAccepted = gson.fromJson(offer, Offer.class);
        } else {
            open.twoAccepted = true;
            open.playerTwoAccepted = gson.fromJson(offer, Offer.class);
        }
        if (open.oneAccepted && open.twoAccepted) {
            lastTrades.log(open);
            trades.delete(tradeId);
            return ok("Trade Done!");
        }
        trades.update(open);
        return ok(gson.toJson(open));
    }

    public Result decline(String tradeId) {
        trades.delete(tradeId);
        return ok();
    }


    /**
     * For testing purposes only!
     *
     * @param tradeId
     * @param firebaseId
     * @param puzzleId
     * @param x
     * @param y
     * @return
     */
    public Result addOffer(String tradeId, String firebaseId, String puzzleId, Integer x, Integer y) {
        Trade open = trades.getTrade(tradeId);
        String test = "{\n" +
                "        \"img_1\": [\n" +
                "            [0, 0, 0, 0],\n" +
                "            [0, -17, 0, 0],\n" +
                "            [0, 0, 0, 0],\n" +
                "            [0, 0, 0, 0]\n" +
                "        ]\n" +
                "    }";
        open.playerOneTradeItems = gson.fromJson(test, Offers.class);
        trades.update(open);
        return ok(gson.toJson(open));
    }
}
