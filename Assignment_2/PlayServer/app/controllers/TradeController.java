package controllers;


import com.google.gson.Gson;

import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.HashMap;

import javax.inject.Inject;

import models.Friendship;
import models.FriendshipRepository;
import models.Inventory;
import models.InventoryRepository;
import models.Puzzle;
import models.PuzzleRepository;
import models.User;
import models.UsersRepository;
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
    @Inject
    private PuzzleRepository puzzles;
    @Inject
    private FriendshipRepository friendships;
    @Inject
    private UsersRepository users;

    public Result getOpenTrade(String firebaseId) {
        Trade openTrade = trades.getTrade(firebaseId);
        return ok(gson.toJson(openTrade));
    }

    public Result getLastTrade(String tradeId) {
        LastTrade lastTrade = lastTrades.getLastTrade(tradeId);
        return ok(gson.toJson(lastTrade));
    }

    public Result getOpenTradeTwoIds(String firebaseId, String partnerId) {
        Trade openTrade = trades.getTradeWithBothIds(firebaseId, partnerId);
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
                if (lastTrade.playerOne.equals(firebaseId)) {
                    trade.traderId = lastTrade.playerOne;
                    trade.partnerId = lastTrade.playerTwo;
                } else {
                    trade.partnerId = lastTrade.playerOne;
                    trade.traderId = lastTrade.playerTwo;
                }
            } else {
                return ok("FAIL");
            }
        } else {
            trade.id = new ObjectId().toString();
            trade.traderId = firebaseId;
            trade.partnerId = partnerId;
        }
        trade.traderTradeItems = new HashMap<>();
        trade.partnerTradeItems = new HashMap<>();
        trade.traderAccepted = false;
        trade.partnerAccepted = false;
        trade.traderOffer = new Offer();
        trade.partnerOffer = new Offer();
        trades.insert(trade);
        return ok(gson.toJson(trade));
    }

    public Result trade(String tradeId, String firebaseId, String offer) {
        return ok();
    }

    public Result offer(String tradeId, String firebaseId, String offers) {
        Trade open = trades.getTradeById(tradeId);
        if (open.traderId.equals(firebaseId)) {
            open.traderTradeItems = gson.fromJson(offers, Offers.class);
        } else {
            open.partnerTradeItems = gson.fromJson(offers, Offers.class);
        }
        open.traderAccepted = false;
        open.partnerAccepted = false;
        open.traderOffer = new Offer();
        open.partnerOffer = new Offer();
        trades.update(open);
        return ok(gson.toJson(open));
    }

    public Result accept(String tradeId, String firebaseId, String offer) {
        Trade open = trades.getTradeById(tradeId);
        if (open.traderId.equals(firebaseId)) {
            open.traderAccepted = true;
            open.traderOffer = gson.fromJson(offer, Offer.class);
        } else {
            open.partnerAccepted = true;
            open.partnerOffer = gson.fromJson(offer, Offer.class);
        }
        if (open.traderAccepted && open.partnerAccepted) {
            lastTrades.log(open);
            Inventory inventoryOne = inventories.getInventory(open.traderId);
            Inventory inventoryTwo = inventories.getInventory(open.partnerId);
            Offer acceptedByTwo = open.partnerOffer;
            Offer acceptedByOne = open.traderOffer;
            //Adjust inventory of playerOne
            inventoryOne.sets.get(acceptedByTwo.setId)[acceptedByTwo.x][acceptedByTwo.y] -= 1;
            if (inventoryOne.sets.containsKey(acceptedByOne.setId)) {
                inventoryOne.sets.get(acceptedByOne.setId)[acceptedByOne.x][acceptedByOne.y] += 1;
            } else {
                Puzzle puzzle = puzzles.getPuzzle(acceptedByOne.setId);
                int[][] inventory = new int[puzzle.piecesCountHorizontal][puzzle.piecesCountVertical];
                inventory[acceptedByOne.x][acceptedByOne.y] = 1;
                inventoryOne.sets.put(acceptedByOne.setId, inventory);
            }
            inventoryOne.cleanUp();
            inventories.update(inventoryOne);
            //Adjust inventory of playerTwo
            inventoryTwo.sets.get(acceptedByOne.setId)[acceptedByOne.x][acceptedByOne.y] -= 1;
            if (inventoryTwo.sets.containsKey(acceptedByTwo.setId)) {
                inventoryTwo.sets.get(acceptedByTwo.setId)[acceptedByTwo.x][acceptedByTwo.y] += 1;
            } else {
                Puzzle puzzle = puzzles.getPuzzle(acceptedByTwo.setId);
                int[][] inventory = new int[puzzle.piecesCountHorizontal][puzzle.piecesCountVertical];
                inventory[acceptedByTwo.x][acceptedByTwo.y] = 1;
                inventoryTwo.sets.put(acceptedByTwo.setId, inventory);

            }
            inventoryTwo.cleanUp();
            inventories.update(inventoryTwo);
            //Update users
            User userOne = users.getUser(open.traderId);
            User userTwo = users.getUser(open.partnerId);
            Friendship friendship = friendships.getFriendshipByIds(open.traderId, open.partnerId);
            if (friendship != null) {
                friendship.updateRank();
                friendships.update(friendship);
                userOne.xp += 100 + 100 * friendship.rank;
                userTwo.xp += 100 + 100 * friendship.rank;
            } else {
                userOne.xp += 100;
                userTwo.xp += 100;
            }
            users.update(userOne);
            users.update(userTwo);
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
        open.traderTradeItems = gson.fromJson(test, Offers.class);
        trades.update(open);
        return ok(gson.toJson(open));
    }
}
