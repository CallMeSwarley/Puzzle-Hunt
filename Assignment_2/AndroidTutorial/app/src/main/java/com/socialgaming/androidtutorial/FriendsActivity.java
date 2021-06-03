package com.socialgaming.androidtutorial;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Models.Friendship;
import com.socialgaming.androidtutorial.Models.FriendshipRank;
import com.socialgaming.androidtutorial.Models.User;
import com.socialgaming.androidtutorial.Util.HTTPGetter;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.zip.Inflater;

public class FriendsActivity extends AppCompatActivity {
    private final Gson gson = new Gson();

    @RequiresApi(api = Build.VERSION_CODES.N)
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
                            HTTPGetter getFriend = new HTTPGetter();
                            getFriend.execute("user", fs.friendOne, "getUser");
                            try {
                                String getFriendResult = getFriend.get();
                                if (!getFriendResult.equals("{ }")) {
                                    User friend = gson.fromJson(getFriendResult, User.class);
                                    text.append(friend.id);
                                    text2.append(fsLvlStr);
                                    child.setId(counter);
                                    //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    if (counter == 0) {
                                        //params.addRule(RelativeLayout.ALIGN_TOP,friends_layout.getId());
                                        friends_layout.addView(child);
                                        System.out.println("Created Row: " + counter);

                                    } else {
                                        //params.addRule(RelativeLayout.BELOW, counter - 1);
                                        //params.addRule(RelativeLayout.ABOVE, R.id.addFriendButton);
                                        friends_layout.addView(child);
                                        System.out.println("Created Row: " + counter);
                                    }
                                }
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else if (!fs.friendTwo.equals(myID)) {
                            HTTPGetter getFriend = new HTTPGetter();
                            getFriend.execute("user", fs.friendTwo, "getUser");
                            try {
                                String getFriendResult = getFriend.get();
                                if (!getFriendResult.equals("{ }")) {
                                    User friend = gson.fromJson(getFriendResult, User.class);
                                    text.append(friend.id);
                                    text2.append(fsLvlStr);
                                    child.setId(counter);
                                    //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    if (counter == 0) {
                                        //params.addRule(RelativeLayout.ALIGN_TOP,friends_layout.getId());
                                        friends_layout.addView(child);
                                        System.out.println("Created Row: " + counter);
                                    } else {
                                        //params.addRule(RelativeLayout.BELOW, counter - 1);
                                        //params.addRule(RelativeLayout.ABOVE, R.id.addFriendButton);
                                        friends_layout.addView(child);
                                        System.out.println("Created Row: " + counter);
                                    }
                                }
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        counter++;
                    }//ende for (Friendship fs : friendlist) -Loop
                    View button = getLayoutInflater().inflate(R.layout.add_friend_button, null);
                    friends_layout.addView(button);

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


}