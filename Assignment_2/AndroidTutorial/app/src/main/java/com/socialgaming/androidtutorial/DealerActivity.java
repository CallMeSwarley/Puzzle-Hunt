package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Adapters.PieceListAdapter;
import com.socialgaming.androidtutorial.Models.Inventory;
import com.socialgaming.androidtutorial.Models.PieceViewItem;
import com.socialgaming.androidtutorial.Models.Puzzle;
import com.socialgaming.androidtutorial.Models.PuzzleModel;
import com.socialgaming.androidtutorial.Models.PuzzlePiece;
import com.socialgaming.androidtutorial.Util.HTTPGetter;
import com.socialgaming.androidtutorial.Util.HTTPPoster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class DealerActivity extends AppCompatActivity {

    // Database stuff
    Inventory inventory = new Inventory();
    Map<String, int[][]> sets = new HashMap<>();
    Gson gson = new Gson();

    // Player 1
    private List<PieceViewItem> playerOneItemList = new ArrayList<>();
    private PieceListAdapter playerOneAdapter;

    //popup
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private RecyclerView piecesView;
    private Button btnAdd;
    private Button btnClose;
    private List<PieceViewItem> popUpItemList = new ArrayList<>();
    private PieceListAdapter popUpAdapter;
    private Button selectPiece=null;

    private Button gamblePiece=null;

    private RecyclerView playerTradeItems=null;

    private ImageView wonPiece1;

    private ImageView wonPiece2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer);
        selectPiece = findViewById(R.id.select_piece_button);
        gamblePiece = findViewById(R.id.play_button);
        wonPiece1 = findViewById(R.id.imageView2);
        wonPiece2 = findViewById(R.id.imageView3);
        TextView winText = findViewById(R.id.textView2);
        gamblePiece.setEnabled(false);
        playerTradeItems = findViewById(R.id.recyclerView2);
        fetchPieces();

        // Player 1 list of pieces recyclerView
        playerTradeItems.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        playerOneAdapter = new PieceListAdapter(playerTradeItems, this, playerOneItemList);
        playerTradeItems.setAdapter(playerOneAdapter);

        selectPiece.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewPiecesAddingDialog();
            }
        });

        gamblePiece.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int random = new Random().nextInt(101);
                PieceViewItem itemStore = playerOneItemList.get(0);
                deletePiece(itemStore.getSetId(),itemStore.getHorizontalPosition(),itemStore.getVerticalPosition());
                if(random>50){
                    winText.setText("you won");
                    addRandomPiece(itemStore.getSetId());
                }else{
                    winText.setText("you lost");
                }
                removePieceFromTradeView(0);
                refreshList();
            }
        });


    }

    public void refreshList(){
        popUpItemList.clear();
        fetchPieces();
        playerTradeItems.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        playerOneAdapter = new PieceListAdapter(playerTradeItems, this, playerOneItemList);
        playerTradeItems.setAdapter(playerOneAdapter);
    }


    public void createNewPiecesAddingDialog(){
        dialogBuilder = new AlertDialog.Builder(this);

        // View
        View addPiecesPopupView = getLayoutInflater().inflate(R.layout.trade_popup, null);
        piecesView = addPiecesPopupView.findViewById(R.id.add_pieces_recyclerView);
        btnClose = addPiecesPopupView.findViewById(R.id.close_button);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        piecesView.setLayoutManager(gridLayoutManager);

        // Adapter
        popUpAdapter = new PieceListAdapter(piecesView, this, popUpItemList);
        piecesView.setAdapter(popUpAdapter);

        dialogBuilder.setView(addPiecesPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    public void addPieceToTradeView(int bindingAdapterPosition) {
        playerOneItemList.add(popUpItemList.remove(bindingAdapterPosition));
        playerOneAdapter.notifyDataSetChanged();
        popUpAdapter.notifyDataSetChanged();
        selectPiece.setEnabled(false);
        gamblePiece.setEnabled(true);
        dialog.dismiss();
    }

    public void removePieceFromTradeView(int bindingAdapterPosition){
        popUpItemList.add(playerOneItemList.remove(bindingAdapterPosition));
        playerOneAdapter.notifyDataSetChanged();
        popUpAdapter.notifyDataSetChanged();
        selectPiece.setEnabled(true);
        gamblePiece.setEnabled(false);
    }

    private void fetchPieces(){

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

        if(sets.isEmpty()){
            insertDummyValues();
        }

        // Aus den Datenbankeinträgen werden hier ViewItems erstellt
        sets.entrySet().stream().forEach(x -> {

            int[][] arr = x.getValue();
            int imageId = getResources().getIdentifier("com.socialgaming.androidtutorial:drawable/" + x.getKey(), null, null);
            Bitmap image = BitmapFactory.decodeResource(this.getResources(), imageId);
            Puzzle puzzle = new Puzzle(x.getKey(), arr.length, arr.length, image);
            for(int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr[i].length; j++) {
                    if (arr[i][j] > 0) {
                        PuzzlePiece piece = puzzle.getPuzzlePiece(i, j);

                        // The piece is added as often as the player has it
                        for (int k = 0; k < arr[i][j]; k++) {
                            PieceViewItem item = new PieceViewItem(piece.getImage(),1,x.getKey(),i,j);
                            popUpItemList.add(item);
                        }
                    }
                }
            }
        });
    }

    private void deletePiece(String id,int x, int y){
        HTTPPoster get = new HTTPPoster();
        get.execute("inventory", FirebaseAuth.getInstance().getUid(), id,Integer.toString(x),Integer.toString(y),"1","removePiece");

    }

    private void addRandomPiece(String id){


        int[] size = getSizeOfPuzzle(id);
        int resId = this.getResources().getIdentifier(id, "drawable", this.getPackageName());
        Drawable image = getResources().getDrawable(resId);
        Bitmap returnedBitmap = ((BitmapDrawable) image).getBitmap();
        Puzzle puzzle = new Puzzle(id, size[0], size[1], returnedBitmap);


        int randomX = new Random().nextInt(size[0]);
        int randomY = new Random().nextInt(size[1]);
        wonPiece1.setImageBitmap(puzzle.getPuzzlePiece(randomX,randomY).getImage());
        int randomX2 = new Random().nextInt(size[0]);
        int randomY2 = new Random().nextInt(size[1]);

        wonPiece2.setImageBitmap(puzzle.getPuzzlePiece(randomX2,randomY2).getImage());
        HTTPPoster get = new HTTPPoster();
        get.execute("inventory", FirebaseAuth.getInstance().getUid(), id,Integer.toString(randomX),Integer.toString(randomY),"1","addPiece");

        HTTPPoster get2 = new HTTPPoster();
        get2.execute("inventory", FirebaseAuth.getInstance().getUid(), id,Integer.toString(randomX2),Integer.toString(randomY2),"1","addPiece");


    }

    private int[] getSizeOfPuzzle(String id){
        int[] ret = new int[2];
        HTTPGetter get = new HTTPGetter();
        get.execute("puzzle",id,"getPuzzle");
        try {
            String getUserResult = get.get();
            if (!getUserResult.equals("{ }")) {
                PuzzleModel puzzle = gson.fromJson(getUserResult, PuzzleModel.class);
                ret[0] = puzzle.piecesCountHorizontal;
                ret[1] = puzzle.piecesCountVertical;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private void insertDummyValues(){

        sets.put("meme", new int[][]{ {1, 2, 1}, {2, 0, 1}, {1, 0, 0}});
        sets.put("img_1", new int[][]{{0, 1, 2, 0}, {3, 1, 2, 1}, {1, 0, 0, 2}, {1, 3, 2, 1}});
    }
}