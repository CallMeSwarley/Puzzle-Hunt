package controllers;


import play.mvc.Controller;
import play.mvc.Result;

public class TradeController extends Controller {
    public Result getOpenTrade(String firebaseId) {
        return ok();
    }

    public Result trade(String tradeId, String offer) {
        return ok();
    }

    public Result offer(String tradeId, String offers) {
        return ok();

    }

    public Result accept(String firebaseId, String offer) {
        return ok();
    }

    public Result decline(String firebaseId) {
        return ok();
    }
}
