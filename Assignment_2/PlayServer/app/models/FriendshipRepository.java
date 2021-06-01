package models;

import org.bson.types.ObjectId;
import org.jongo.MongoCollection;

import javax.inject.Inject;
import javax.inject.Singleton;

import uk.co.panaxiom.playjongo.PlayJongo;

@Singleton
public class FriendshipRepository {
    @Inject
    private PlayJongo jongo;

    private static FriendshipRepository instance = null;

    public FriendshipRepository() {
        instance = this;
    }

    public static FriendshipRepository getInstance() {
        return instance;
    }

    public MongoCollection friendships() {
        MongoCollection friendshipCollection = jongo.getCollection("friendships");
        return friendshipCollection;
    }

    public Friendship getFriendship(String id) {
        return friendships().findOne("{_id: #}", id).as(Friendship.class);
    }

    public String insert(Friendship fs) {
        fs.id = new ObjectId().toString();
        friendships().save(fs);
        return fs.id;
    }

    public void delete(String generatedFsID) {
        friendships().remove(generatedFsID);
    }

    public void update(Friendship fs) {
        friendships().update("{_id: #}", fs.id).with(copyFriendship(fs));
    }

    public Friendship copyFriendship(Friendship fs) {
        Friendship copy = new Friendship(fs.friendOne, fs.friendTwo);
        copy.rank = fs.rank;
        copy.friendshipStart = fs.friendshipStart;
        return copy;
    }
}
