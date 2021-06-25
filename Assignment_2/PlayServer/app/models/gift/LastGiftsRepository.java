package models.gift;

import org.jongo.MongoCollection;

import java.time.LocalDate;

import javax.inject.Inject;

import uk.co.panaxiom.playjongo.PlayJongo;

public class LastGiftsRepository {
    @Inject
    PlayJongo jongo;
    private static LastGiftsRepository instance = null;

    public LastGiftsRepository() {
        instance = this;
    }

    public static LastGiftsRepository getInstance() {
        return instance;
    }

    public MongoCollection gifts() {
        return jongo.getCollection("gifts");
    }

    public LastGift getLastGift(String friendshipId) {
        return gifts().findOne("{_id:#}", friendshipId).as(LastGift.class);
    }


    public void insert(String friendshipId) {
        LastGift lastGift = new LastGift();
        lastGift.id = friendshipId;
        LocalDate now = LocalDate.now();
        lastGift.year = now.getYear();
        lastGift.dayOfYear = now.getDayOfYear();
        gifts().save(lastGift);
    }

    public void update(LastGift lastGift) {
        gifts().update("{_id:#}", lastGift.id).with(copy(lastGift));
    }

    public LastGift copy(LastGift lastGift) {
        LastGift copy = new LastGift();
        copy.id = lastGift.id;
        copy.dayOfYear = lastGift.dayOfYear;
        copy.year = lastGift.year;
        return copy;
    }

    public void log(Gift gift) {
        LastGift lastGift = this.getLastGift(gift.friendshipID);
        if (lastGift == null) {
            insert(gift.friendshipID);
        } else {
            LocalDate now = LocalDate.now();
            lastGift.year = now.getYear();
            lastGift.dayOfYear = now.getDayOfYear();
            update(lastGift);
        }
    }
}
