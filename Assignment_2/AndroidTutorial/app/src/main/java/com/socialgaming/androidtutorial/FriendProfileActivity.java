package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Models.PuzzleModel;
import com.socialgaming.androidtutorial.Models.User;
import com.socialgaming.androidtutorial.Util.HTTPGetter;
import com.socialgaming.androidtutorial.Util.HTTPPoster;

import java.util.Random;
import java.util.concurrent.ExecutionException;

public class FriendProfileActivity extends AppCompatActivity {
    private final Gson gson = new Gson();
    public static String id = "";
    public static String name = "";
    public static Long xp = Long.valueOf(0);
    public static String lvl = "";
    public static String friendshipLvl = "";
    public static String description = "";
    public static String friendshipID = "";
    public static int friendshipLevelInt = 0;
    public static int profilePicId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);


        final ImageView profilePic = findViewById(R.id.profile_pic_image);
        if (profilePicId > 0)
            profilePic.setImageResource(profilePicId);
        else
            profilePic.setImageResource(R.drawable.avatar);

        //final TextInputLayout idView = findViewById(R.id.id_textView);
        //final EditText id = idView.getEditText();

        final TextInputLayout nameView = findViewById(R.id.name_textView);
        final EditText name = nameView.getEditText();

        final TextInputLayout xpView = findViewById(R.id.xp_textView);
        final EditText xp = xpView.getEditText();

        final TextInputLayout lvlView = findViewById(R.id.lvl_textView);
        final EditText lvl = lvlView.getEditText();

        final TextInputLayout friendshipLvlView = findViewById(R.id.friendship_lvl_textView);
        final EditText friendshipLvl = friendshipLvlView.getEditText();

        final TextInputLayout descriptionView = findViewById(R.id.description_textView);
        final EditText description = descriptionView.getEditText();

        final TextView title = findViewById(R.id.main_text);
        String titleString = FriendProfileActivity.name + "'s Profile";
        if (!FriendProfileActivity.name.equals("")) {
            title.setText(titleString);
        }

        name.setText(FriendProfileActivity.name);
        xp.setText("" + FriendProfileActivity.xp);
        lvl.setText(FriendProfileActivity.lvl);
        friendshipLvl.setText(FriendProfileActivity.friendshipLvl);
        description.setText(FriendProfileActivity.description);

        final Button sendGift = findViewById(R.id.send_gift_button);
        final Button trade = findViewById(R.id.trade_button2);
        final Button removeFriend = findViewById(R.id.remove_friend_button);

        sendGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FriendProfileActivity.friendshipLevelInt < 2) {
                    Toast.makeText(FriendProfileActivity.this, "Your friendshiplevel is too low to send gifts!", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(FriendProfileActivity.this).create();
                    alertDialog.setTitle("Send Gift");
                    alertDialog.setMessage("Do you want to send a special gift to " + FriendProfileActivity.name + "?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Random rand = new Random();
                            String PuzzleId = "";
                            int x = 0;
                            int y = 0;
                            //Random Puzzlepiece zum senden holen
                            HTTPGetter getPuzzles = new HTTPGetter();
                            getPuzzles.execute("puzzle", "getAll");
                            try {
                                String puzzleResult = getPuzzles.get();
                                if (!puzzleResult.equals("{ }")) {
                                    PuzzleModel[] puzzles = gson.fromJson(puzzleResult, PuzzleModel[].class);
                                    int randId = rand.nextInt(puzzles.length);
                                    PuzzleId = puzzles[randId].id;
                                    x = rand.nextInt(puzzles[randId].piecesCountHorizontal);
                                    y = rand.nextInt(puzzles[randId].piecesCountVertical);
                                }
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                            //Höheres fs-lvl=mehr inhalt im gift
                            HTTPPoster post = new HTTPPoster();
                            post.execute("gifts", FriendProfileActivity.friendshipID, FirebaseAuth.getInstance().getUid(), PuzzleId, "" + x, "" + y, "" + FriendProfileActivity.friendshipLevelInt, "sendGift");
                            try {
                                String sendGiftResult = post.get();
                                if (sendGiftResult.equals("{ }")) {
                                    Toast.makeText(FriendProfileActivity.this, "Sth. went wrong, try again Later", Toast.LENGTH_SHORT).show();
                                } else if (sendGiftResult.equals("Already sent gift in the last 24 hours")) {
                                    Toast.makeText(FriendProfileActivity.this, "You can only send a gift every 24 hours", Toast.LENGTH_SHORT).show();
                                } else {
                                    HTTPGetter get = new HTTPGetter();
                                    get.execute("user", FirebaseAuth.getInstance().getUid(), "getUser");
                                    try {
                                        String getUserResult = get.get();
                                        if (!getUserResult.equals("{ }")) {
                                            User user = gson.fromJson(getUserResult, User.class);
                                            user.xp += 50;
                                            new HTTPPoster().execute(
                                                    "user",
                                                    Uri.encode(gson.toJson(user, User.class)),//necessary to escape "unsafe" characters, otherwise error in play framework
                                                    "update");
                                        }
                                    } catch (ExecutionException | InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(FriendProfileActivity.this, "Gift was sent successfully! Earned +50 XP", Toast.LENGTH_SHORT).show();
                                }
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            }
        });

        trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TradeActivity.partnerId = FriendProfileActivity.id;
                TradeActivity.friendshipLvl = FriendProfileActivity.friendshipLvl;
                TradeActivity.partnerXp = FriendProfileActivity.xp;
                TradeActivity.partnerName = FriendProfileActivity.name;
                Intent intent = new Intent(FriendProfileActivity.this, TradeActivity.class);
                startActivity(intent);
            }
        });
        removeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(FriendProfileActivity.this).create();
                alertDialog.setTitle("Remove friend");
                alertDialog.setMessage("Remove " + FriendProfileActivity.name + "?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new HTTPPoster().execute("friendship", FriendProfileActivity.friendshipID, "removeFriendship");
                        HTTPGetter get = new HTTPGetter();
                        get.execute("user", FriendProfileActivity.id, "getUser");
                        try {
                            String getUserResult = get.get();
                            if (!getUserResult.equals("{ }")) {
                                User friend = gson.fromJson(getUserResult, User.class);
                                friend.friends.remove(FriendProfileActivity.friendshipID);
                                new HTTPPoster().execute(
                                        "user",
                                        Uri.encode(gson.toJson(friend, User.class)),//necessary to escape "unsafe" characters, otherwise error in play framework
                                        "update");
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        HTTPGetter getMe = new HTTPGetter();
                        getMe.execute("user", FirebaseAuth.getInstance().getUid(), "getUser");
                        try {
                            String erg = getMe.get();
                            if (!erg.equals("{ }")) {
                                User me = gson.fromJson(erg, User.class);
                                me.friends.remove(FriendProfileActivity.friendshipID);
                                new HTTPPoster().execute(
                                        "user",
                                        Uri.encode(gson.toJson(me, User.class)),//necessary to escape "unsafe" characters, otherwise error in play framework
                                        "update");
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(FriendProfileActivity.this, FriendsActivity.class);
                        startActivity(intent);
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
    }
}