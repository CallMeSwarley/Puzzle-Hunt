package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InventoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);


        final Button sets = findViewById(R.id.sets_button);
        final Button pieces = findViewById(R.id.pieces_button);

        sets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryActivity.this, SetsActivity.class);
                startActivity(intent);
            }
        });

        pieces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryActivity.this, PiecesActivity.class);
                startActivity(intent);
            }
        });
    }
}