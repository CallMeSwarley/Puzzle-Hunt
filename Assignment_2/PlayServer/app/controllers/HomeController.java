package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import models.Friendship;
import models.FriendshipRepository;
import models.Location;
import models.LocationsRepository;
import models.User;
import models.UsersRepository;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {
    private final Gson gson = new Gson();
    @Inject
    private LocationsRepository locations;
    @Inject
    private UsersRepository users;
    @Inject
    private FriendshipRepository friendships;

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok("Ok");
    }

    public Result updateUser(String userString) {
        Logger.info("Update user");

        User user = gson.fromJson(userString, User.class);
        users.update(user);
        return ok("User updated");
    }

    public Result test(String firebaseId) {
        Result res;

        Location location = new Location(firebaseId, new Double[]{48.218800, 11.624707});
        location.updateLocation(new Double[]{48.149101, 11.567317});
        location.updateLocation(new Double[]{48.262617, 11.668276});
        ObjectNode searchResults = Json.newObject();
        ArrayNode locationArray = searchResults.arrayNode();
        ObjectNode locationsNode = Json.newObject();
        locationsNode.put("user", location.id);
        locationsNode.put("loc1", location.loc1[0]);
        locationsNode.put("loc12", location.loc1[1]);
        locationsNode.put("loc2", location.loc2[0]);
        locationsNode.put("loc22", location.loc2[1]);
        locationsNode.put("loc3", location.loc3[0]);
        locationsNode.put("loc32", location.loc3[1]);
        locationArray.add(locationsNode);
        searchResults.put("locations", locationArray);
        Logger.info(gson.toJson(location));

        return ok("test");
    }

    public Result prepareUser(String firebaseId) {
        Logger.info("prepareUser");
        User user = users.getUser(firebaseId);
        if (user == null) {
            user = new User(firebaseId);
            users.insert(user);
        }
        return ok("User prepared");
    }

    public Result prepareFriendship(String firebaseIdMe, String firebaseIdFriend) {
        Logger.info("prepareFriendship");
        User me = users.getUser(firebaseIdMe);
        User friend = users.getUser(firebaseIdFriend);
        if (me == null || friend == null) {
            return ok("This didn't work");
        } else {
            for (String id : me.friends) {
                Friendship friendship = friendships.getFriendship(id);
                if (friendship.friendOne.equals(firebaseIdFriend) || friendship.friendTwo.equals(firebaseIdFriend)) {
                    return ok("Already Friends");
                }
            }
            Friendship fs=new Friendship(firebaseIdMe,firebaseIdFriend,new ObjectId().toString());
            friendships.insert(fs);
            User user=users.getUser(firebaseIdMe);
            user.addFriend(fs.id);
            users.update(user);
            user=users.getUser(firebaseIdFriend);
            user.addFriend(fs.id);
            users.update(user);
            return ok("Friendship prepared");
        }
    }

    public Result getFriendList(String firebaseId){
        User user=users.getUser(firebaseId);
        List fsIDs=user.friends;
        List friendshipList=new ArrayList<>();
        fsIDs.forEach(x->{
            friendshipList.add(friendships.getFriendship((String) x));
        });
        Result res = ok(gson.toJson(friendshipList));
        return res;
    }


    public Result prepareUserWithNickname(String firebaseId, String nickName) {
        Logger.info("prepareUser with nickname");
        User user = new User(firebaseId, nickName);
        users.insert(user);
        return ok("User prepared");
    }

    public Result updateUserLocation(String firebaseId, Double latitude, Double longitude) {
        Logger.info("updateUserLocation HomeController");
        Location loc = locations.getLocation(firebaseId);
        if (loc != null) {
            loc.updateLocation(new Double[]{latitude, longitude});
            locations.update(loc);
        } else {
            loc = new Location(firebaseId, new Double[]{latitude, longitude});
            locations.insert(loc);
        }
        return ok("Ok");
    }

    public Result getUser(String firebaseId) {
        Result res;
        res = ok(gson.toJson(users.getUser(firebaseId)));
        return res;
    }

    public Result getUserByNickName(String nickName) {
        Result res;
        res = ok(gson.toJson(users.getUserByNickName(nickName)));
        return res;
    }

    public Result getFriendship(String friendshipID){
        Result res;
        res = ok(gson.toJson(friendships.getFriendship(friendshipID)));
        return res;
    }

    public Result getAllLocations(String firebaseId) {
        Result res;

        Location location = locations.getLocation(firebaseId);

        ObjectNode searchResults = Json.newObject();
        ArrayNode locationArray = searchResults.arrayNode();

        ObjectNode locationsNode = Json.newObject();

        locationsNode.put("user", location.id);
        locationsNode.put("loc1", location.loc1[0]);
        locationsNode.put("loc12", location.loc1[1]);
        locationsNode.put("loc2", location.loc2[0]);
        locationsNode.put("loc22", location.loc2[1]);
        locationsNode.put("loc3", location.loc3[0]);
        locationsNode.put("loc32", location.loc3[1]);
        locationArray.add(locationsNode);
        searchResults.put("locations", locationArray);
        res = ok(searchResults);
        return res;
    }

    public Result explore() {
        return ok(views.html.explore.render());
    }

    public Result tutorial() {
        return ok(views.html.tutorial.render());
    }

    public Result remoteTest() {
        return ok("Heroku reached");
    }

    public Result deployTest() {
        return ok("Deploy Successful");
    }

    public Result getNearbyUsers(String firebaseId) {
        Result res;
        res = ok(gson.toJson(locations.getNearbyUsers(firebaseId)));
        return res;
    }
}
