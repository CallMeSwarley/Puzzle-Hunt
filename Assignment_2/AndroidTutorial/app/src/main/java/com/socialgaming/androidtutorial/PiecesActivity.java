package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Adapters.PieceListAdapter;
import com.socialgaming.androidtutorial.Models.Inventory;
import com.socialgaming.androidtutorial.Models.PieceViewItem;
import com.socialgaming.androidtutorial.Models.Puzzle;
import com.socialgaming.androidtutorial.Models.PuzzlePiece;
import com.socialgaming.androidtutorial.Util.HTTPGetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class PiecesActivity extends AppCompatActivity {

    List<PieceViewItem> viewItems = new ArrayList<>();
    PieceListAdapter adapter;

    Inventory inventory = new Inventory();
    Map<String, int[][]> sets = new HashMap<>();
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pieces);

        // Recyclerview
        final RecyclerView pieceView = findViewById(R.id.pieces_recyclerview);
        pieceView.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));

        // Adapter
        adapter = new PieceListAdapter(pieceView, this, viewItems);
        pieceView.setAdapter(adapter);

        // Fetch Data
        fetchDataFromDatabase();
    }

    private void fetchDataFromDatabase() {

        HTTPGetter get = new HTTPGetter();
        get.execute("inventory", FirebaseAuth.getInstance().getUid(), "getInventory");
        try {
            String getUserResult = get.get();
            if (!getUserResult.equals("{ }")) {
                this.inventory = gson.fromJson(getUserResult, Inventory.class);
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
            int imageId = getResources().getIdentifier("com.socialgaming.androidtutorial:drawable/" + x.getKey(), null, null);
            Bitmap image = BitmapFactory.decodeResource(this.getResources(), imageId);
            Puzzle puzzle = new Puzzle(x.getKey(), arr.length, arr.length, image);

            for(int i = 0; i < arr.length; i++){
                for(int j = 0; j < arr[i].length; j++){
                    if(arr[i][j] > 0){

                        PuzzlePiece piece = puzzle.getPuzzlePiece(i, j);
                        PieceViewItem item = new PieceViewItem(piece.getImage(), arr[i][j]);
                        viewItems.add(item);
                    }
                }
            }
        });
    }

    private void insertDummyValues(){

        sets.put("meme", new int[][]{ {1, 2, 1}, {2, 0, 1}, {1, 0, 0}});
        sets.put("img_1", new int[][]{{0, 1, 2, 0}, {3, 1, 2, 1}, {1, 0, 0, 2}, {1, 3, 2, 1}});
    }
}