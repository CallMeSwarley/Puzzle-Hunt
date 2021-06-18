package controllers;

import com.google.gson.Gson;

import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import models.Friendship;
import models.FriendshipRepository;
import models.Puzzle;
import models.PuzzleRepository;
import models.User;
import models.UsersRepository;
import models.gift.Gift;
import models.gift.GiftsRepository;
import play.mvc.Controller;
import play.mvc.Result;

public class GiftController extends Controller {
    @Inject
    UsersRepository users;
    @Inject
    GiftsRepository gifts;
    @Inject
    FriendshipRepository friendships;
    @Inject
    PuzzleRepository puzzles;
    Gson gson = new Gson();

    public Result getGifts(String firebaseId) {
        User user = users.getUser(firebaseId);
        List<String> friendships = user.friends;
        return ok(gson.toJson(
                friendships
                        .stream()
                        .map(fs -> gifts.getGift(fs, firebaseId))
                        .filter(g -> g != null)
                        .toArray(Gift[]::new)
                )
        );
    }

    public Result sendGift(String friendshipId, String userId, String puzzleId, Integer x, Integer y, Integer amount) {
        Friendship friendship = friendships.getFriendship(friendshipId);
        String friendId = friendship.friendOne.equals(userId) ? friendship.friendTwo : friendship.friendOne;
        Gift alreadySent = gifts.getGift(friendshipId, friendId);
        if (alreadySent == null) {
            alreadySent = new Gift();
            alreadySent.id = new ObjectId().toString();
            alreadySent.friendshipID = friendshipId;
            alreadySent.receiverID = friendId;
            alreadySent.content = new HashMap<>();
            Puzzle puzzle = puzzles.getPuzzle(puzzleId);
            int[][] content = new int[puzzle.piecesCountHorizontal][puzzle.piecesCountVertical];
            content[x][y] = amount;
            alreadySent.content.put(puzzleId, content);
            gifts.insert(alreadySent);
        } else {
            Map<String, int[][]> content = alreadySent.content;
            if (content.containsKey(puzzleId)) {
                content.get(puzzleId)[x][y] += amount;
            } else {
                Puzzle puzzle = puzzles.getPuzzle(puzzleId);
                int[][] puzzleContent = new int[puzzle.piecesCountHorizontal][puzzle.piecesCountVertical];
                puzzleContent[x][y] = amount;
                content.put(puzzleId, puzzleContent);
            }
            alreadySent.content = content;
            gifts.update(alreadySent);
        }
        return ok(gson.toJson(alreadySent));
    }

    public Result removeGift(String giftID) {
        gifts.delete(giftID);
        return ok("deleted gift " + giftID);
    }


}
