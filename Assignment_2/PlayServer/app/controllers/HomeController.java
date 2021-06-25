package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import models.Dealer;
import models.DealersRepository;
import models.Friendship;
import models.FriendshipRepository;
import models.Inventory;
import models.InventoryRepository;
import models.Location;
import models.LocationsRepository;
import models.POIsRepository;
import models.Puzzle;
import models.PuzzleRepository;
import models.Shop;
import models.ShopsRepository;
import models.User;
import models.UsersRepository;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Utilities;

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
    @Inject
    private PuzzleRepository puzzles;
    @Inject
    private InventoryRepository inventories;
    @Inject
    private DealersRepository dealers;
    @Inject
    private ShopsRepository shops;
    @Inject
    private POIsRepository pois;

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

    public Result setTitle(String firebaseId, String puzzleId, String title) {
        Inventory userInventory = inventories.getInventory(firebaseId);
        Map<String, String> map = userInventory.titles;
        map.put(puzzleId, title);
        userInventory.titles = map;
        inventories.update(userInventory);
        return ok(gson.toJson(userInventory));
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

    public Result removeFriendship(String fsID) {
        Logger.info("delete Friendship");
        friendships.delete(fsID);
        return ok("Friendship deleted");
    }

    public Result prepareFriendship(String firebaseIdMe, String firebaseIdFriend) {
        Logger.info("prepareFriendship");
        User me = users.getUser(firebaseIdMe);
        User friend = users.getUser(firebaseIdFriend);
        if (me == null || friend == null) {
            return ok("This didn't work");
        } else if (firebaseIdFriend.equals(firebaseIdMe))
            return ok("That's kinda sad :(");
        else {
            /*for (String id : me.friends) {
                Friendship friendship = friendships.getFriendship(id);
                if (friendship.friendOne.equals(firebaseIdFriend) || friendship.friendTwo.equals(firebaseIdFriend)) {
                    return ok("Already Friends");
                }
            }*/
            if (friendships.getFriendshipByIds(firebaseIdMe, firebaseIdFriend) != null) {
                return ok("Already Friends");
            }
            Friendship fs = new Friendship(firebaseIdMe, firebaseIdFriend, new ObjectId().toString());
            friendships.insert(fs);
            User user = users.getUser(firebaseIdMe);
            user.addFriend(fs.id);
            users.update(user);
            user = users.getUser(firebaseIdFriend);
            user.addFriend(fs.id);
            users.update(user);
            return ok("Friendship prepared");
        }
    }

    public Result getFriendList(String firebaseId) {
        User user = users.getUser(firebaseId);
        List<String> fsIDs = user.friends;
        List<Friendship> friendshipList = new ArrayList<>();
        fsIDs.forEach(fsId -> {
            Friendship friendship = friendships.getFriendship(fsId);
            int rankBefore = friendship.rank;
            friendship.updateRank();
            if (friendship.rank != rankBefore) {
                friendships.update(friendship);
            }
            friendshipList.add(friendship);
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

    public Result getFriendship(String friendshipID) {
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

    public Result getPuzzle(String puzzleId) {
        Logger.info("getPuzzle " + puzzleId);
        return ok(gson.toJson((puzzles.getPuzzle(puzzleId))));
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

    public Result getInventory(String firebaseId) {
        Inventory result = inventories.getInventory(firebaseId);
        if (result == null) {
            result = new Inventory();
            result.id = firebaseId;
            inventories.insert(result);
        }
        return ok(gson.toJson(result));
    }

    public Result addPiece(String firebaseId, String puzzleId, Integer x, Integer y, Integer counter) {
        Inventory saved = inventories.getInventory(firebaseId);
        User user = users.getUser(firebaseId);
        if (saved.sets.containsKey(puzzleId)) {
            saved.sets.get(puzzleId)[x][y] += counter;
        } else {
            Puzzle puzzle = puzzles.getPuzzle(puzzleId);
            int[][] set = new int[puzzle.piecesCountHorizontal][puzzle.piecesCountVertical];
            set[x][y] = counter;
            saved.sets.put(puzzleId, set);
        }
        user.xp += 100 * counter;
        inventories.update(saved);
        users.update(user);
        return ok(gson.toJson(saved));
    }

    public Result removePiece(String firebaseId, String puzzleId, Integer x, Integer y, Integer counter) {
        Inventory saved = inventories.getInventory(firebaseId);
        User user = users.getUser(firebaseId);
        if (saved.sets.containsKey(puzzleId)) {
            saved.sets.get(puzzleId)[x][y] -= counter;
            if (saved.sets.get(puzzleId)[x][y] < 0) {
                saved.sets.get(puzzleId)[x][y] = 0;
            }
            boolean allEmpty = true;
            Puzzle puzzle = puzzles.getPuzzle(puzzleId);
            for (int idxX = 0; idxX < puzzle.piecesCountHorizontal && allEmpty; ++idxX)
                for (int idxY = 0; idxY < puzzle.piecesCountVertical && allEmpty; ++idxY)
                    allEmpty = allEmpty && saved.sets.get(puzzleId)[idxX][idxY] <= 0;
            if (allEmpty) {
                saved.sets.remove(puzzleId);
            }
        }

        inventories.update(saved);
        users.update(user);
        return ok(gson.toJson(saved));
    }

    public Result getNicknames() {
        return ok(gson.toJson(users.getNicknames()));
    }

    public Result checkNickname(String nickName) {
        return ok(gson.toJson(
                Arrays.stream(users.getNicknames())
                        .noneMatch(s -> s.equals(nickName))));
    }

    public Result getShop(String id) {
        return ok(gson.toJson(shops.getShop(id)));
    }

    public Result getAllShops() {
        return ok(gson.toJson(shops.getAllShops()));
    }

    public Result getVisibleShops(String firebaseId, Double latSW, Double lonSW, Double latNE, Double lonNE) {
        Double[] lastLocation = locations.getLocation(firebaseId).loc1;
        Shop[] visible = shops.getVisible(latSW, lonSW, latNE, lonNE);
        Logger.info("visibleShops:\t" + gson.toJson(visible));
        return ok(gson.toJson(Arrays
                .stream(visible)
                .filter(d -> !Utilities.inRange(lastLocation[0], lastLocation[1], d.lat, d.lon, d.range))
                .toArray(Shop[]::new)));

    }

    public Result getActiveShops(String firebaseId, Double latSW, Double lonSW, Double latNE, Double lonNE) {
        Double[] lastLocation = locations.getLocation(firebaseId).loc1;
        Shop[] visible = shops.getVisible(latSW, lonSW, latNE, lonNE);
        Logger.info("activeShops:\t" + gson.toJson(visible));
        return ok(gson.toJson(Arrays
                .stream(visible)
                .filter(d -> Utilities.inRange(lastLocation[0], lastLocation[1], d.lat, d.lon, d.range))
                .toArray(Shop[]::new)));

    }

    public Result addShop(String title, Double lat, Double lon, Long range) {
        Shop shop = new Shop();
        shop.title = title;
        shop.id = new ObjectId().toString();
        shop.lat = lat;
        shop.lon = lon;
        shop.range = range;
        shops.insert(shop);
        return ok(gson.toJson(shop));
    }

    public Result getDealer(String id) {
        return ok(gson.toJson(dealers.getDealer(id)));
    }

    public Result getVisibleDealers(String firebaseId, Double latSW, Double lonSW, Double latNE, Double lonNE) {
        Double[] lastLocation = locations.getLocation(firebaseId).loc1;
        Dealer[] visible = dealers.getVisible(latSW, lonSW, latNE, lonNE);
        Logger.info("visibleDealer:\t" + gson.toJson(visible));
        return ok(gson.toJson(Arrays
                .stream(visible)
                .filter(d -> !Utilities.inRange(lastLocation[0], lastLocation[1], d.lat, d.lon, d.range))
                .toArray(Dealer[]::new)));

    }

    public Result getActiveDealers(String firebaseId, Double latSW, Double lonSW, Double latNE, Double lonNE) {
        Double[] lastLocation = locations.getLocation(firebaseId).loc1;
        Dealer[] visible = dealers.getVisible(latSW, lonSW, latNE, lonNE);
        Logger.info("activeDealer:\t" + gson.toJson(visible));
        return ok(gson.toJson(Arrays
                .stream(visible)
                .filter(d -> Utilities.inRange(lastLocation[0], lastLocation[1], d.lat, d.lon, d.range))
                .toArray(Dealer[]::new)));

    }

    public Result addDealer(String title, Double lat, Double lon, Long range) {
        Dealer dealer = new Dealer();
        dealer.id = new ObjectId().toString();
        dealer.title = title;
        dealer.lat = lat;
        dealer.lon = lon;
        dealer.range = range;
        dealers.insert(dealer);
        return ok(gson.toJson(dealer));
    }

    public Result getAllUsers() {
        return ok(gson.toJson(users.getAll()));
    }

    public Result getNearbyPOIs(Double lat, Double lon) {
        return ok(gson.toJson(pois.getNearbyPOIs(lat, lon)));
    }

    public Result getAllPuzzles() {
        return ok(gson.toJson(puzzles.getAll()));
    }
}
