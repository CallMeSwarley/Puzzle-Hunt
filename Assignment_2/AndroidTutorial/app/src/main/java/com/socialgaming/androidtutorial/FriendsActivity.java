package com.socialgaming.androidtutorial;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Models.Friendship;
import com.socialgaming.androidtutorial.Models.User;
import com.socialgaming.androidtutorial.Util.HTTPGetter;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class FriendsActivity extends AppCompatActivity {
    private final Gson gson = new Gson();
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        //in friend_row.xml ist ein freundes eintrag enthalten, dieser sollte dann einfach aufgerufen
        // und angezeigt werden (mehrfach, um ne freundesliste anzuzeigen)
        //im activity_friends sind nur platzhalter für ein ca. layout

        final Button addFriends = findViewById(R.id.add_friends_button);
        addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsActivity.this, AddFriendsActivity.class);
                startActivity(intent);
            }
        });
        //nur zum testen sollte durch referenzen auf buttons aus friend_row.xml ersetzt werden
        final Button testViewProfile = findViewById(R.id.test_view_profile_button);
        final Button testTrade = findViewById(R.id.test_trade_button);

        //nur zum testen sollte durch referenzen auf buttons aus friend_row.xml ersetzt werden
        testViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsActivity.this, FriendProfileActivity.class);
                startActivity(intent);
            }
        });

        testTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsActivity.this, TradeActivity.class);
                startActivity(intent);
            }
        });
        //Freundesliste holen
        TextView freundeListe = findViewById(R.id.freundesListe);
        freundeListe.setText("");
        HTTPGetter get = new HTTPGetter();
        get.execute("user", FirebaseAuth.getInstance().getUid(), "getFriendList");
        try {
            String getUserResult = get.get();
            if (!getUserResult.equals("{ }")) {
                List friendlist = gson.fromJson(getUserResult, List.class);
                if(friendlist!=null){
                    friendlist.forEach(x->{
                        Friendship fs= (Friendship) x;
                        String myID=FirebaseAuth.getInstance().getUid();
                        //Freund holen
                        if(fs.friendOne!=myID){
                            HTTPGetter getFriend = new HTTPGetter();
                            getFriend.execute("user", fs.friendOne, "getUser");
                            try {
                                String getFriendResult = getFriend.get();
                                if (!getFriendResult.equals("{ }")) {
                                    User friend = gson.fromJson(getFriendResult, User.class);
                                    freundeListe.append(friend.id+"\n");
                                }
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }else if(fs.friendTwo!=myID){
                            HTTPGetter getFriend = new HTTPGetter();
                            getFriend.execute("user", fs.friendTwo, "getUser");
                            try {
                                String getFriendResult = getFriend.get();
                                if (!getFriendResult.equals("{ }")) {
                                    User friend = gson.fromJson(getFriendResult, User.class);
                                    freundeListe.append(friend.id+"\n");
                                }
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });//ende friendlist.forEach-Loop
                }else{
                    System.out.println("Get Friendslist didn't work!");
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}