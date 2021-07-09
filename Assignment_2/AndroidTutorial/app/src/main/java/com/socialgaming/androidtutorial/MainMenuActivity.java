package com.socialgaming.androidtutorial;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Models.Gift;
import com.socialgaming.androidtutorial.Models.PuzzleModel;
import com.socialgaming.androidtutorial.Util.HTTPGetter;
import com.socialgaming.androidtutorial.Util.HTTPPoster;

import java.util.concurrent.ExecutionException;

public class MainMenuActivity extends AppCompatActivity {
    private final Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        final Button myProfile = findViewById(R.id.my_profile_button);
        final Button inventory = findViewById(R.id.inventory_button);
        final Button puzzleMap = findViewById(R.id.puzzle_map_button);
        final Button friends = findViewById(R.id.friends_button);
        final Button leaderboard = findViewById(R.id.leaderboard_button);
//        final Button events = findViewById(R.id.events_button);


        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, MyProfileActivity.class);
                startActivity(intent);
            }
        });
        inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, InventoryActivity.class);
                startActivity(intent);
            }
        });

        puzzleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, PuzzleMapActivity.class);
                startActivity(intent);
            }
        });
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, FriendsActivity.class);
                startActivity(intent);
            }
        });
        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, LeaderboardActivity.class);
                startActivity(intent);
            }
        });
//        events.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainMenuActivity.this, PuzzleShopActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(Build.VERSION.SDK) > 5 && keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        } else return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainMenuActivity.this).create();
        alertDialog.setTitle("Logout");
        alertDialog.setMessage("Doing this will log you out");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}