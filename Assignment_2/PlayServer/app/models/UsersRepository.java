package models;

import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import uk.co.panaxiom.playjongo.PlayJongo;

@Singleton
public class UsersRepository {
    @Inject
    private PlayJongo jongo;

    private static UsersRepository instance = null;

    public UsersRepository() {
        instance = this;
    }

    public static UsersRepository getInstance() {
        return instance;
    }

    public MongoCollection users() {
        MongoCollection locationCollection = jongo.getCollection("users");
        return locationCollection;
    }

    public User getUser(String id) {
        User user = users().findOne("{nickName:#}", id).as(User.class);
        if (user == null)
            return users().findOne("{_id: #}", id).as(User.class);
        return user;
    }

    public User getUserByNickName(String nickName) {
        return users().findOne("{_nickName: #}", nickName).as(User.class);
    }

    public void insert(User user) {
        users().save(user);
    }

    public void update(User user) {
        users().update("{_id: #}", user.id).with(copyUser(user));
    }

    public User copyUser(User user) {
        User copy = new User(user.id, user.nickName);
        copy.description = user.description;
        copy.friends = user.friends;
        copy.xp = user.xp;
        return copy;
    }

    public String[] getNicknames() {
        MongoCursor<User> users = users().find().as(User.class);
        List<User> userObjects = new ArrayList();
        for (User u : users) {
            userObjects.add(u);
        }
        return userObjects.stream()
                .map(x -> x.nickName)
                .toArray(String[]::new);
    }

    public User[] getAll() {
        MongoCursor<User> users = users().find().as(User.class);
        List<User> userObjects = new ArrayList();
        for (User u : users) {
            userObjects.add(u);
        }
        return userObjects.stream().toArray(User[]::new);
    }
}
