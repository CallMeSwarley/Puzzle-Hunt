package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.os.Bundle;

public class SetsActivity extends AppCompatActivity {

    boolean showCompleted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        final Button btnShowCompleted = findViewById(R.id.showCompleted_button);
        final Button btnSortBy = findViewById(R.id.sortBy_button);

        //final ImageButton

        btnShowCompleted.setText("Show completed: " + (showCompleted ? "Yes" : "No"));

        btnShowCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCompleted = !showCompleted;

                btnShowCompleted.setText("Show completed: " + (showCompleted ? "Yes" : "No"));
            }
        });

        btnSortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        final RecyclerView sets = findViewById(R.id.sets_recyclerView);
    }

    private enum sortOptions {
        MostPieces,
        LeastPieces,
        Alphabetical,

    }
}