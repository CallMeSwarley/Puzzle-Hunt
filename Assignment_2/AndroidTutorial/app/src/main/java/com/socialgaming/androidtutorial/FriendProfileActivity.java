package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);


        final ImageView profilePic = findViewById(R.id.profile_pic_image);

        final TextView id = findViewById(R.id.id_textView);
        final TextView name = findViewById(R.id.name_textView);
        final TextView xp = findViewById(R.id.xp_textView);
        final TextView lvl = findViewById(R.id.lvl_textView);

        final TextView friendshipLvl = findViewById(R.id.friendship_lvl_textView);

        final TextView description = findViewById(R.id.description_textView);
        final TextView descriptionText = findViewById(R.id.description_text_textView);
        final TextView achievement = findViewById(R.id.achievement_textView);

        //können in ne liste umgewandelt werden oder so, je nach geschmack, ist nur als platzhalter da
        final ImageView achievement1 = findViewById(R.id.achievement1_image);
        final ImageView achievement2 = findViewById(R.id.achievement2_image);


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
                Intent intent = new Intent(FriendProfileActivity.this, TradeActivity.class);
                startActivity(intent);
            }
        });
        removeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendProfileActivity.this, RemoveFriendActivity.class);
                startActivity(intent);
            }
        });

    }
}