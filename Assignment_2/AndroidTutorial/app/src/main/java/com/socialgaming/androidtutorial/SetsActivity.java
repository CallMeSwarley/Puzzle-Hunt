package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.Toast;

import com.socialgaming.androidtutorial.Adapters.SetListAdapter;
import com.socialgaming.androidtutorial.Interfaces.ILoadMore;
import com.socialgaming.androidtutorial.Models.SetViewItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class SetsActivity extends AppCompatActivity {

    boolean showCompleted = true;

    List<SetViewItem> items = new ArrayList<>();
    List<SetViewItem> completedItems = new ArrayList<>();
    SetListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        // Buttons
        final Button btnSortBy = findViewById(R.id.sortBy_button);

        // Switch
        final Switch switchShowCompleted = findViewById(R.id.showCompleted_switch);

        createRandomData(10);

        // RecyclerView und Adapter
        final RecyclerView setList = findViewById(R.id.sets_recyclerview);
        setList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SetListAdapter(setList, this, items);
        setList.setAdapter(adapter);

        // Mehr items laden event
        adapter.setLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {
                if(items.size() <= 50) { // Anstatt einer Konstanten die gesamte Anzahl der vorhandenen Puzzles
                    items.add(null);
                    adapter.notifyItemInserted(items.size() - 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            items.remove(items.size() - 1);
                            adapter.notifyItemRemoved(items.size());

                            createRandomData(10);

                            adapter.notifyDataSetChanged();
                            adapter.setLoaded();
                        }
                    }, 5000);
                }
            }
        });

        // Vollständige Puzzles anzeigen
        switchShowCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                // Vollständige Puzzles aus oder einblenden

                if(b){
                    // Einblenden
                    items.addAll(completedItems);
                    completedItems.clear();
                    adapter.notifyDataSetChanged();
                }
                else{
                    // Ausblenden
                    completedItems = items.stream().filter(x -> x.getOwnedPieces() == x.getMaxPieces()).collect(Collectors.toList());
                    items.removeIf(x -> x.getOwnedPieces() == x.getMaxPieces());
                    adapter.notifyDataSetChanged();
                }
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

                        switch (menuItem.getItemId()) {

                            case R.id.item_mostPieces:
                                // Sortieren
                                Collections.sort(items, SetViewItem.MostPiecesComperator);
                                adapter.notifyDataSetChanged();
                                btnSortBy.setText("Sort by: Most Pieces");
                                return true;

                            case R.id.item_leastPieces:
                                // Sortieren
                                Collections.sort(items, SetViewItem.LeastPiecesComperator);
                                adapter.notifyDataSetChanged();
                                btnSortBy.setText("Sort by: Least Pieces");
                                return true;

                            case R.id.item_alphabetical:
                                // Sortieren
                                Collections.sort(items, SetViewItem.AlphabeticalComperator);
                                adapter.notifyDataSetChanged();
                                btnSortBy.setText("Sort by: Alphabetical");
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


    // Random Daten zum Testen
    private void createRandomData(int count){

        for (int i = 0; i < count; i++){

            Random r = new Random();
            int pieces = r.nextInt(17);

            SetViewItem item = new SetViewItem("Hallo", pieces, 16);
            items.add(item);
        }
    }
}