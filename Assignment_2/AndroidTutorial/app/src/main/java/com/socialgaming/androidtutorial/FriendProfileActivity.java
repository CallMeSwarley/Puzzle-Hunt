package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.socialgaming.androidtutorial.Models.User;
import com.socialgaming.androidtutorial.Util.HTTPGetter;
import com.socialgaming.androidtutorial.Util.HTTPPoster;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);


        final ImageView profilePic = findViewById(R.id.profile_pic_image);


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
                Intent intent = new Intent(FriendProfileActivity.this, SendGiftActivity.class);
                startActivity(intent);
            }
        });

        trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TradeActivity.id = FriendProfileActivity.id;
                TradeActivity.friendshipLvl = FriendProfileActivity.friendshipLvl;
                TradeActivity.xp = FriendProfileActivity.xp;
                TradeActivity.name = FriendProfileActivity.name;
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
                        new HTTPPoster().execute("friendship", FriendProfileActivity.friendshipID, "removeFriendship");
                        Intent intent = new Intent(FriendProfileActivity.this, FriendsActivity.class);
                        startActivity(intent);                    }
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