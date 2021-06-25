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
        return jongo.getCollection("lastGifts");
    }

    public LastGift getLastGift(String id) {
        return gifts().findOne("{_id:#}", id).as(LastGift.class);
    }

    public LastGift getLastGift(String friendshipId, String receiverId) {
        return gifts().findOne("{friendship:#,receiverId:#}", friendshipId, receiverId).as(LastGift.class);
    }


    public void insert(String id, String friendshipId, String receiverId) {
        LastGift lastGift = new LastGift();
        lastGift.id = id;
        LocalDate now = LocalDate.now();
        lastGift.year = now.getYear();
        lastGift.dayOfYear = now.getDayOfYear();
        lastGift.receiverId = receiverId;
        lastGift.friendship = friendshipId;
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
        copy.friendship = lastGift.friendship;
        copy.receiverId = lastGift.receiverId;
        return copy;
    }

    public void log(Gift gift) {
        LastGift lastGift = this.getLastGift(gift.id);
        if (lastGift == null) {
            insert(gift.id, gift.friendshipID, gift.receiverID);
        } else {
            LocalDate now = LocalDate.now();
            lastGift.year = now.getYear();
            lastGift.dayOfYear = now.getDayOfYear();
            update(lastGift);
        }
    }
}
