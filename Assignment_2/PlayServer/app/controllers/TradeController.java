package controllers;


import com.google.gson.Gson;

import javax.inject.Inject;

import models.trade.TradesRepository;
import play.mvc.Controller;
import play.mvc.Result;

public class TradeController extends Controller {
    private final Gson gson = new Gson();
    @Inject
    private TradesRepository trades;

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
