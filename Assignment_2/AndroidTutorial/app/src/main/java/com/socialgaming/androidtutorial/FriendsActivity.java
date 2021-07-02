package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Models.Friendship;
import com.socialgaming.androidtutorial.Models.FriendshipRank;
import com.socialgaming.androidtutorial.Models.User;
import com.socialgaming.androidtutorial.Util.HTTPGetter;

import java.util.Random;
import java.util.concurrent.ExecutionException;

public class FriendsActivity extends AppCompatActivity {
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        //Freundesliste holen
        HTTPGetter get = new HTTPGetter();
        get.execute("user", FirebaseAuth.getInstance().getUid(), "getFriendList");
        try {
            String getUserResult = get.get();
            if (!getUserResult.equals("{ }")) {
                Friendship[] friendshipArr = gson.fromJson(getUserResult, Friendship[].class);
                if (friendshipArr != null) {
                    LinearLayout friends_layout = findViewById(R.id.friendsActivity);
                    int counter = 0;
                    for (Friendship fs : friendshipArr) {
                        View child = getLayoutInflater().inflate(R.layout.friend_row, null);
                        TextView text = child.findViewById(R.id.friend_name_textView);
                        TextView text2 = child.findViewById(R.id.friend_lvl_textView);
                        String fsLvlStr = "";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            fs.updateRank();
                        }
                        if (fs.rank == 0)
                            fsLvlStr = FriendshipRank.FRIENDLY_GREETINGS.toString();
                        if (fs.rank == 1)
                            fsLvlStr = FriendshipRank.CO_PUZZLERS.toString();
                        if (fs.rank == 2)
                            fsLvlStr = FriendshipRank.PUZZLE_BUDDIES.toString();
                        if (fs.rank == 3)
                            fsLvlStr = FriendshipRank.PUZZLE_BFF.toString();
                        if (fs.rank == 4)
                            fsLvlStr = FriendshipRank.PUZZLE_SOULMATES.toString();
                        String myID = FirebaseAuth.getInstance().getUid();
                        //Freund holen
                        if (!fs.friendOne.equals(myID)) {
                            createXMLRow(fs.friendOne, fsLvlStr, fs.id, fs.rank);
                        } else if (!fs.friendTwo.equals(myID)) {
                            createXMLRow(fs.friendTwo, fsLvlStr, fs.id, fs.rank);
                        }
                    }//ende for (Friendship fs : friendlist) -Loop
                    //View button = getLayoutInflater().inflate(R.layout.add_friend_button, null);
                    //friends_layout.addView(button);
                } else {
                    System.out.println("Get Friendslist didn't work!");
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        final Button addFriends = findViewById(R.id.add_friends_button);
        addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsActivity.this, AddFriendsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FriendsActivity.this, MainMenuActivity.class);
        startActivity(intent);
    }

    private void createXMLRow(String friendID, String fsLvlStr, String fsID, int fsLvlInt) {
        LinearLayout friends_layout = findViewById(R.id.friendsActivity);
        View child = getLayoutInflater().inflate(R.layout.friend_row, null);
        TextView text = child.findViewById(R.id.friend_name_textView);
        TextView text2 = child.findViewById(R.id.friend_lvl_textView);
        Button viewFriendProfile = child.findViewById(R.id.view_profile_button);
        Button tradeWithFriend = child.findViewById(R.id.trade_button);
        ImageView friendPic=child.findViewById(R.id.friend_pic_imageView);
        int randomOfTwoInts = new Random().nextBoolean() ? 1 : 2;
        int picID=-1;
        if(randomOfTwoInts==1){
            friendPic.setImageResource(R.drawable.profile_pic1);
            picID=R.drawable.profile_pic1;
        }else {
            friendPic.setImageResource(R.drawable.profile_pic2);
            picID=R.drawable.profile_pic2;
        }
        HTTPGetter getFriend = new HTTPGetter();
        getFriend.execute("user", friendID, "getUser");
        try {
            String getFriendResult = getFriend.get();
            if (!getFriendResult.equals("{ }")) {
                User friend = gson.fromJson(getFriendResult, User.class);
                if (friend.nickName != "")
                    text.append(friend.nickName);
                else
                    text.append(friend.id);
                text2.append(fsLvlStr);
                int finalPicID = picID;
                viewFriendProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FriendProfileActivity.id = friendID;
                        FriendProfileActivity.friendshipLvl = fsLvlStr;
                        FriendProfileActivity.xp = friend.xp;
                        FriendProfileActivity.name = friend.nickName;
                        FriendProfileActivity.description = friend.description;
                        FriendProfileActivity.friendshipID = fsID;
                        FriendProfileActivity.friendshipLevelInt = fsLvlInt;
                        FriendProfileActivity.profilePicId= finalPicID;
                        Intent intent = new Intent(FriendsActivity.this, FriendProfileActivity.class);
                        startActivity(intent);
                    }
                });
                tradeWithFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TradeActivity.partnerId = friendID;
                        TradeActivity.friendshipLvl = fsLvlStr;
                        TradeActivity.partnerXp = friend.xp;
                        TradeActivity.partnerName = friend.nickName;
                        Intent intent = new Intent(FriendsActivity.this, TradeActivity.class);
                        startActivity(intent);
                    }
                });
                friends_layout.addView(child);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}