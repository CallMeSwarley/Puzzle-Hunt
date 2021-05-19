package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class FriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        //in friend_row.xml ist ein freundes eintrag enthalten, dieser sollte dann einfach aufgerufen und angezeigt werden (mehrfach, um ne freundesliste anzuzeigen)
        //im activity_friends sind nur platzhalter für ein ca. layout
    }
}