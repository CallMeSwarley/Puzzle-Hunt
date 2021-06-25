package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Adapters.PieceListAdapter;
import com.socialgaming.androidtutorial.Models.Inventory;
import com.socialgaming.androidtutorial.Models.Offer;
import com.socialgaming.androidtutorial.Models.PieceViewItem;
import com.socialgaming.androidtutorial.Models.Puzzle;
import com.socialgaming.androidtutorial.Models.PuzzlePiece;
import com.socialgaming.androidtutorial.Models.Trade;
import com.socialgaming.androidtutorial.Util.HTTPGetter;
import com.socialgaming.androidtutorial.Util.HTTPPoster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TradeActivity extends AppCompatActivity {

    private final int TRADE_PIECE_AMOUNT = 1;

    // Database stuff
    Inventory inventory = new Inventory();
    Trade trade = new Trade();
    Map<String, int[][]> sets = new HashMap<>();
    Gson gson = new Gson();

    // Player
    private List<PieceViewItem> playerItemList = new ArrayList<>();
    private PieceListAdapter playerAdapter;

    // Trading Partner
    private List<PieceViewItem> partnerItemList = new ArrayList<>();
    private PieceListAdapter partnerAdapter;

    // Popup
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private RecyclerView piecesView;
    private Button btnClose;
    private List<PieceViewItem> popUpItemList = new ArrayList<>();
    private PieceListAdapter popUpAdapter;

    // Trading partners information
    public static String partnerId = "";
    public static String partnerName = "";
    public static Long partnerXp = Long.valueOf(0);
    public static String friendshipLvl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        final RecyclerView playerTradeItems = findViewById(R.id.player1_trade_items_recyclerView);
        final RecyclerView partnerTradeItems = findViewById(R.id.player2_trade_items_recyclerView);
        final Button addPieces = findViewById(R.id.add_pieces_button);
        final Button refreshView = findViewById(R.id.refresh_button);
        final Button acceptTrade = findViewById(R.id.accept_trade_button);
        final Button declineTrade = findViewById(R.id.decline_trade_button);

        // Popup list of pieces recyclerView
        fetchPieces();

        // Player 1 list of pieces recyclerView
        playerTradeItems.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        playerAdapter = new PieceListAdapter(playerTradeItems, this, playerItemList, false);
        playerTradeItems.setAdapter(playerAdapter);

        // Player 2 list of pieces recyclerView
        partnerTradeItems.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        partnerAdapter = new PieceListAdapter(partnerTradeItems, this, partnerItemList, false);
        partnerTradeItems.setAdapter(partnerAdapter);

        // Setup trade in database
        try{
            new HTTPPoster().execute(
                    "trade",
                    FirebaseAuth.getInstance().getUid(),
                    this.partnerId,
                    "beginTrade");

            // Pull created trade instance in database to safe locally
            HTTPGetter getter = new HTTPGetter();
            getter.execute("trade", FirebaseAuth.getInstance().getUid(), "getOpenTrade");

            String getUserResult = getter.get();
            if (!getUserResult.equals("{ }")) {
                trade = gson.fromJson(getUserResult, Trade.class);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // Opens the popup to look for pieces to trade
        addPieces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewPiecesAddingDialog();
            }
        });

        // Refreshes the trading partners view
        refreshView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HTTPGetter getter = new HTTPGetter();
                    getter.execute("trade", FirebaseAuth.getInstance().getUid(), "getOpenTrade");
                    String getUserResult = getter.get();
                    if (!getUserResult.equals("{ }")) {
                        trade = gson.fromJson(getUserResult, Trade.class);
                        trade.playerTwoTradeItems.entrySet().stream().forEach(x -> processPieces(x, partnerItemList));
                        partnerAdapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(getBaseContext(), "Trade canceled by " + partnerName, Toast.LENGTH_SHORT).show();
                        this.wait(1000);
                        finish();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        // Accept the trade, wait for partner to accept as well
        acceptTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    HTTPGetter getter = new HTTPGetter();
                    getter.execute("trade", FirebaseAuth.getInstance().getUid(), "getOpenTrade");
                    String getUserResult = getter.get();

                    if (getUserResult.equals("{ }")) {
                        Toast.makeText(getBaseContext(), "Trade canceled by " + partnerName, Toast.LENGTH_SHORT).show();
                        this.wait(1000);
                        finish();
                    }

                    trade = gson.fromJson(getUserResult, Trade.class);
                    trade.oneAccepted = true;
                    trade.playerOneAccepted = new Offer();
                    addPieces.setEnabled(false);
                    playerTradeItems.setEnabled(false);

                    if(playerItemList.size() > 0){
                        PieceViewItem piece = playerItemList.get(0);
                        trade.playerOneAccepted.setId = piece.getSetId();
                        trade.playerOneAccepted.x = piece.getHorizontalPosition();
                        trade.playerOneAccepted.y = piece.getVerticalPosition();
                    }

                    // /trade/:tradeId/:firebaseId/:offer/accept
                    HTTPPoster poster = new HTTPPoster();
                    poster.execute(
                            "trade",
                            trade.getId(),
                            FirebaseAuth.getInstance().getUid(),
                            Uri.encode(gson.toJson(trade.playerOneAccepted, Offer.class)),
                            "accept");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        // Decline the trade, leave the activity, send decline to database
        declineTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    new HTTPPoster().execute("trade", trade.id, "decline");
                    finish();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void createNewPiecesAddingDialog() {
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
                updatePlayerOneDatabase();
                dialog.dismiss();
            }
        });
    }

    public void addPieceToTradeView(int bindingAdapterPosition) {
        if(playerItemList.size() >= TRADE_PIECE_AMOUNT) {
            Toast.makeText(this, "You are only allowed to trade " + TRADE_PIECE_AMOUNT + (TRADE_PIECE_AMOUNT == 1 ? " piece." : " pieces."), Toast.LENGTH_SHORT).show();
            return;
        }

        playerItemList.add(popUpItemList.remove(bindingAdapterPosition));
        playerAdapter.notifyDataSetChanged();
        popUpAdapter.notifyDataSetChanged();

        if (popUpItemList.isEmpty())
            dialog.dismiss();
    }

    public void removePieceFromTradeView(int bindingAdapterPosition){
        if(trade.oneAccepted){
            Toast.makeText(this, "You accepted the trade, you can't change your offer anymore", Toast.LENGTH_LONG).show();
            return;
        }

        popUpItemList.add(playerItemList.remove(bindingAdapterPosition));
        updatePlayerOneDatabase();
        playerAdapter.notifyDataSetChanged();
        popUpAdapter.notifyDataSetChanged();
    }

    private void updatePlayerOneDatabase() {
        try {


//            ******* CODE FOR MULTIPLE PIECE TRADING, NOT NEEDED RIGHT NOW *******

//            trade.playerOneTradeItems.clear();
//            for (int i = 0; i < playerOneItemList.size(); i++) {
//                PieceViewItem item = playerOneItemList.get(i);
//
//                int[][] entry;
//                if(trade.playerOneTradeItems.containsKey(item.getSetId())) {
//                    entry = trade.playerOneTradeItems.get(item.getSetId());
//                }
//                else {
//                    int dim = sets.get(item.getSetId()).length;
//                    entry = new int[dim][dim];
//                }
//
//                entry[item.getHorizontalPosition()][item.getVerticalPosition()]++;
//                trade.playerOneTradeItems.put(item.getSetId(), entry);
//            }

            // Code for single piece trading
            trade.playerOneTradeItems.clear();
            PieceViewItem item = playerItemList.get(0);
            int dim = sets.get(item.getSetId()).length;
            int[][] entry = new int[dim][dim];
            entry[item.getHorizontalPosition()][item.getVerticalPosition()]++;
            trade.playerOneTradeItems.put(item.getSetId(), entry);

            //trade/:tradeId/:firebaseId/:offerList/offer

            String test = gson.toJson(trade.playerOneTradeItems);

            new HTTPPoster().execute(
                    "trade",
                    trade.getId(),
                    FirebaseAuth.getInstance().getUid(),
                    Uri.encode(gson.toJson(trade.playerOneTradeItems)),
                    "offer");

        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(this, "Error trying to access database...", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchPieces(){
        HTTPGetter getter = new HTTPGetter();
        getter.execute("inventory", FirebaseAuth.getInstance().getUid(), "getInventory");
        try {
            String getUserResult = getter.get();
            if (!getUserResult.equals("{ }")) {
                this.inventory = gson.fromJson(getUserResult, Inventory.class);
                this.sets = inventory.getSets();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sets.entrySet().stream().forEach(x -> processPieces(x, popUpItemList));
    }

    private void processPieces(Map.Entry<String, int[][]> x, List<PieceViewItem> itemList){
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
                        PieceViewItem item = new PieceViewItem(piece.getImage(), x.getKey(), i, j);
                        itemList.add(item);
                    }
                }
            }
        }
    }
}
