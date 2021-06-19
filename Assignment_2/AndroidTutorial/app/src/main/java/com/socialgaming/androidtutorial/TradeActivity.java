package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Adapters.PieceListAdapter;
import com.socialgaming.androidtutorial.Models.Inventory;
import com.socialgaming.androidtutorial.Models.PieceViewItem;
import com.socialgaming.androidtutorial.Models.Puzzle;
import com.socialgaming.androidtutorial.Models.PuzzlePiece;
import com.socialgaming.androidtutorial.Models.Trade;
import com.socialgaming.androidtutorial.Models.TradeItem;
import com.socialgaming.androidtutorial.Util.HTTPGetter;
import com.socialgaming.androidtutorial.Util.HTTPPoster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TradeActivity extends AppCompatActivity {

    // Database stuff
    Inventory inventory = new Inventory();
    Map<String, int[][]> sets = new HashMap<>();
    Gson gson = new Gson();
    HTTPPoster poster = new HTTPPoster();
    HTTPGetter getter = new HTTPGetter();

    // Player 1
    private List<PieceViewItem> playerOneItemList = new ArrayList<>();
    private List<TradeItem> pOnePieceInformation = new ArrayList<>();
    private PieceListAdapter playerOneAdapter;

    // Player 2
    private Map<String, int[][]> pTwoPieceInformation;

    // Popup
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private RecyclerView piecesView;
    private Button btnClose;
    private List<PieceViewItem> popUpItemList = new ArrayList<>();
    private PieceListAdapter popUpAdapter;

    // Stuff
    public static String id = "";
    public static String name = "";
    public static Long xp = Long.valueOf(0);
    public static String friendshipLvl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        final RecyclerView playerTradeItems = findViewById(R.id.player1_trade_items_recyclerView);
        final RecyclerView friendTradeItems = findViewById(R.id.player2_trade_items_recyclerView);
        final Button addPieces = findViewById(R.id.add_pieces_button);
        final Button refreshView = findViewById(R.id.refresh_button);
        final Button acceptTrade = findViewById(R.id.accept_trade_button);
        final Button declineTrade = findViewById(R.id.decline_trade_button);

        // Popup list of pieces recyclerView
        fetchPieces();
//        Bitmap bm = BitmapFactory.decodeResource(this.getResources(), R.drawable.img_1);
//        Bitmap bm1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.meme);
//        popUpItemList.add(new PieceViewItem(bm));
//        popUpItemList.add(new PieceViewItem(bm1));

        // Player 1 list of pieces recyclerView
        playerTradeItems.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        playerOneAdapter = new PieceListAdapter(playerTradeItems, this, playerOneItemList, false);
        playerTradeItems.setAdapter(playerOneAdapter);

        // TODO Player 2 list of pieces recyclerView

        addPieces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewPiecesAddingDialog();
            }
        });

        refreshView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
        popUpAdapter = new PieceListAdapter(piecesView, this, popUpItemList, true);
        piecesView.setAdapter(popUpAdapter);

        dialogBuilder.setView(addPiecesPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                HTTPPoster poster = new HTTPPoster();

            }
        });
    }

    public void addPieceToTradeView(int bindingAdapterPosition) {
        PieceViewItem item = popUpItemList.remove(bindingAdapterPosition);
        item.setListingPosition(playerOneItemList.size());
        playerOneItemList.add(item);

        playerOneAdapter.notifyDataSetChanged();
        popUpAdapter.notifyDataSetChanged();

        if (popUpItemList.isEmpty())
            dialog.dismiss();
    }

    public void removePieceFromTradeView(int bindingAdapterPosition){
        PieceViewItem item = playerOneItemList.remove(bindingAdapterPosition);
        item.setListingPosition(popUpItemList.size());
        popUpItemList.add(item);

        playerOneAdapter.notifyDataSetChanged();
        popUpAdapter.notifyDataSetChanged();
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

            for(int i = 0; i < arr.length; i++){
                for(int j = 0; j < arr[i].length; j++){
                    if(arr[i][j] > 0){
                        PuzzlePiece piece = puzzle.getPuzzlePiece(i, j);

                        // The piece is added as often as the player has it
                        for(int k = 0; k < arr[i][j]; k++){
                            PieceViewItem item = new PieceViewItem(piece.getImage());
                            item.setListingPosition(popUpItemList.size());
                            item.setSetId(x.getKey());
                            item.setHorizontalPosition(i);
                            item.setVerticalPosition(j);
                            popUpItemList.add(item);

                            // TradeItem is used for internally mapping the position of the piece in the RecyclerView
                            // to the Information of the piece that is needed when trading since the click event
                            // on the RecyclerView only returns the position within its list
                            //pOnePieceInformation.add(new TradeItem(popUpItemList.size() - 1, x.getKey(), i, j));
                        }
                    }
                }
            }
        });
    }

    private void insertDummyValues(){

        sets.put("meme", new int[][]{ {1, 2, 1}, {2, 0, 1}, {1, 0, 0}});
        sets.put("img_1", new int[][]{{0, 1, 2, 0}, {3, 1, 2, 1}, {1, 0, 0, 2}, {1, 3, 2, 1}});
    }

    // TODO exp erhöhen wenn der trade erfolgreich war

    // TODO boni für trades je nach freundeslevel, (z.B. Anzahl der Teile die man traden kann)
}
