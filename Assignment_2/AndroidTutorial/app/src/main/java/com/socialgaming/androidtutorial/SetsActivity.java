package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Adapters.SetListAdapter;
import com.socialgaming.androidtutorial.Interfaces.ILoadMore;
import com.socialgaming.androidtutorial.Models.Inventory;
import com.socialgaming.androidtutorial.Models.Puzzle;
import com.socialgaming.androidtutorial.Models.SetViewItem;
import com.socialgaming.androidtutorial.Models.User;
import com.socialgaming.androidtutorial.Util.HTTPGetter;
import com.socialgaming.androidtutorial.Util.HTTPPoster;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SetsActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.socialgaming.androidtutorial.id";

    //CardView cardview =findViewById(R.id.set_CardView);

    List<SetViewItem> items = new ArrayList<>();
    List<SetViewItem> completedItems = new ArrayList<>();
    SetListAdapter adapter;



    // Eventuel Name des Puzzles
    Inventory inventory = new Inventory();
    Map<String, String> titles = new HashMap<>();
    Map<String, int[][]> sets = new HashMap<>();

    private Puzzle puzzle;
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        // Buttons
        final Button btnSortBy = findViewById(R.id.sortBy_button);

        // Switch
        final Switch switchShowCompleted = findViewById(R.id.showCompleted_switch);

        fetchSetInformation();

        //createRandomData(10);

        // RecyclerView und Adapter
        final RecyclerView setView = findViewById(R.id.sets_recyclerview);
        LinearLayoutManager lr = new LinearLayoutManager(this);
        setView.setLayoutManager(lr);

        adapter = new SetListAdapter(setView, this, items, new Function<Puzzle, Void>() {
            @Override
            public Void apply(Puzzle puzzle) {
                openCollection(puzzle);
                return null;
            }

        });

        setView.setAdapter(adapter);

        // Mehr items laden event
//        adapter.setLoadMore(new ILoadMore() {
//            @Override
//            public void onLoadMore() {
//                if(items.size() <= sets.size()) {
//                    items.add(null);
//                    adapter.notifyItemInserted(items.size() - 1);
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            items.remove(items.size() - 1);
//                            adapter.notifyItemRemoved(items.size());
//
//                            createRandomData(10);
//
//                            adapter.notifyDataSetChanged();
//                            adapter.setLoaded();
//                        }
//                    }, 2000);
//                }
//            }
//        });

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

//        btnSortBy.setOnClickListener(v -> {
//            new HTTPPoster().execute(
//                    "inventory",
//                    FirebaseAuth.getInstance().getUid(),
//                    "meme",
//                    "This is a meme",
//                    "setTitle");
//        });

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
                                Collections.sort(items, SetViewItem.MostPiecesComparator);
                                adapter.notifyDataSetChanged();
                                btnSortBy.setText("Sort by: Most Pieces");
                                return true;

                            case R.id.item_leastPieces:
                                // Sortieren
                                Collections.sort(items, SetViewItem.LeastPiecesComparator);
                                adapter.notifyDataSetChanged();
                                btnSortBy.setText("Sort by: Least Pieces");
                                return true;

                            case R.id.item_alphabetical:
                                // Sortieren
                                Collections.sort(items, SetViewItem.AlphabeticalComparator);
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


    private void fetchSetInformation() {

        HTTPGetter get = new HTTPGetter();
        get.execute("inventory", FirebaseAuth.getInstance().getUid(), "getInventory");
        try {
            String getUserResult = get.get();
            if (!getUserResult.equals("{ }")) {
                this.inventory = gson.fromJson(getUserResult, Inventory.class);
                this.titles = inventory.getTitles();
                this.sets = inventory.getSets();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        if(sets.isEmpty())
//            insertDummyValues();

        // Aus den Datenbankeinträgen werden hier ViewItems erstellt
        sets.entrySet().stream().forEach(x -> {

            int[][] arr = x.getValue();
            int maxPieces = arr.length * arr.length;
            int ownedPieces = (int) Arrays.stream(arr).flatMap(i -> Arrays.stream(i).boxed()).filter(i -> i > 0).count();

            int imageId = getResources().getIdentifier("com.socialgaming.androidtutorial:drawable/" + x.getKey(), null, null);
            List<Integer> listownedPieces = new ArrayList<>();
            for(int i=0;i<arr.length;i++){
                for(int j=0;j<arr.length;j++){
                    if(arr[i][j]>0){
                        listownedPieces.add(i+j*arr.length);
                    }
                }
            }

            //items.add(new SetViewItem(x.getKey(), imageId, listownedPieces.size(), maxPieces, listownedPieces));
            items.add(new SetViewItem(titles.get(x.getKey()), imageId, listownedPieces.size(), maxPieces, listownedPieces));
        });
    }

    private void insertDummyValues() {

        sets.put("meme", new int[][]{{1, 2, 1}, {2, 0, 1}, {1, 0, 0}});
        sets.put("img_1", new int[][]{{0, 1, 2, 0}, {3, 1, 2, 1}, {1, 0, 0, 2}, {1, 3, 2, 1}});

        titles.put("meme", "This is a dummy entry");
        titles.put("img_1", "This is a dummy entry");
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

    private void openCollection(Puzzle puzzle){
        Intent intent = new Intent(this, CollectionsActivity.class);
        intent.putExtra(EXTRA_ID, puzzle.toString());
        startActivity(intent);
    }
}