package models.gift;

import org.jongo.MongoCollection;

import javax.inject.Inject;

import uk.co.panaxiom.playjongo.PlayJongo;

public class GiftsRepository {

    @Inject
    PlayJongo jongo;
    private static GiftsRepository instance = null;

    public GiftsRepository() {
        instance = this;
    }

    public static GiftsRepository getInstance() {
        return instance;
    }

    public MongoCollection gifts() {
        return jongo.getCollection("gifts");
    }

    public Gift getGift(String friendshipId, String receiverId) {
        return gifts().findOne("{friendshipID:#,receiverID:#}", friendshipId, receiverId).as(Gift.class);
    }


    public void insert(Gift gift) {
        gifts().save(gift);
    }

    public void update(Gift gift) {
        gifts().update("{_id:#}", gift.id).with(gift);
    }

    public void delete(String giftID) {
        gifts().remove("{_id:#}", giftID);
    }

    public Gift copyGift(Gift gift) {
        Gift copy = new Gift();
        copy.id = gift.id;
        copy.receiverID = gift.receiverID;
        copy.content = gift.content;
        return copy;
    }
}
