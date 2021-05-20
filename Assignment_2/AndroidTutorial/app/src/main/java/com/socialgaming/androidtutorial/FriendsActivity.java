package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        //in friend_row.xml ist ein freundes eintrag enthalten, dieser sollte dann einfach aufgerufen und angezeigt werden (mehrfach, um ne freundesliste anzuzeigen)
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

    }
}