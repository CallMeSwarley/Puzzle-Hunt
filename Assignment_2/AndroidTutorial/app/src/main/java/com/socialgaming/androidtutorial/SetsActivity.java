package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.socialgaming.androidtutorial.Models.RVAdapter;

public class SetsActivity extends AppCompatActivity {

    String[] s1;
    int[] images = {R.drawable.common_full_open_on_phone, R.drawable.common_google_signin_btn_icon_dark, R.drawable.common_google_signin_btn_icon_dark_normal_background,
    R.drawable.common_google_signin_btn_icon_light_focused, R.drawable.googleg_standard_color_18, R.drawable.common_google_signin_btn_icon_light_normal_background,
    R.drawable.common_full_open_on_phone, R.drawable.common_google_signin_btn_icon_disabled, R.drawable.common_google_signin_btn_icon_light_normal };

    boolean showCompleted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        // Buttons
        final Button btnShowCompleted = findViewById(R.id.showCompleted_button);
        final Button btnSortBy = findViewById(R.id.sortBy_button);

        // RecyclerView
        final RecyclerView setList = findViewById(R.id.sets_recyclerview);

        // Recourcen laden
        s1 = getResources().getStringArray(R.array.puzzle_sets);

        RVAdapter adapter = new RVAdapter(this, s1, images);
        setList.setAdapter(adapter);
        setList.setLayoutManager(new LinearLayoutManager(this));


        //btnSortBy.setText("Sort by: " + sortOptions.toString().replace('_', ' '));
        btnShowCompleted.setText("Show completed: " + (showCompleted ? "Yes" : "No"));

        // Vollständig Puzzles anzeigen
        btnShowCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCompleted = !showCompleted;
                btnShowCompleted.setText("Show completed: " + (showCompleted ? "Yes" : "No"));
            }
        });

        // Sortierbutton
        btnSortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(SetsActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()){

                            case R.id.item_mostPieces:
                                btnSortBy.setText("Sort by: Most Pieces");
                                // Sortieren
                                return true;
                            case R.id.item_leastPieces:
                                btnSortBy.setText("Sort by: Least Pieces");
                                // Sortieren
                                return true;
                            case R.id.item_alphabetical:
                                btnSortBy.setText("Sort by: Alphabetical");
                                // Sortieren
                                return true;
                            default:
                                return false;

                        }
                    }
                });

                popupMenu.show();
            }
        });



    }
}