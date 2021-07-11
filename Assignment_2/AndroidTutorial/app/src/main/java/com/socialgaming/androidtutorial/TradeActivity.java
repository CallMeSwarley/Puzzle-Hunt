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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Adapters.PieceListAdapter;
import com.socialgaming.androidtutorial.Models.Inventory;
import com.socialgaming.androidtutorial.Models.LastTrade;
import com.socialgaming.androidtutorial.Models.Offer;
import com.socialgaming.androidtutorial.Models.PieceViewItem;
import com.socialgaming.androidtutorial.Models.Puzzle;
import com.socialgaming.androidtutorial.Models.PuzzlePiece;
import com.socialgaming.androidtutorial.Models.Trade;
import com.socialgaming.androidtutorial.Util.HTTPGetter;
import com.socialgaming.androidtutorial.Util.HTTPPoster;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TradeActivity extends AppCompatActivity {

    // Database stuff
    Trade trade = new Trade();
    Map<String, int[][]> sets = new HashMap<>();
    Gson gson = new Gson();
    Role role = Role.Unasigned;

    private PieceViewItem selectedPiece;
    private Trade lastTradeState;

    // Player
    private List<PieceViewItem> playerItemList = new ArrayList<>();
    private PieceListAdapter playerAdapter;

    // Trading Partner
    private List<PieceViewItem> partnerItemList = new ArrayList<>();
    private PieceListAdapter partnerAdapter;

    // Popup
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private RecyclerView tradePopupRecyclerView;
    private Button btnClose;
    private List<PieceViewItem> popUpItemList = new ArrayList<>();
    private PieceListAdapter popUpAdapter;

    // Trading partners information
    public static String partnerId = "";
    public static String partnerName = "";
    public static Long partnerXp = Long.valueOf(0);
    public static String friendshipLvl = "";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            new HTTPPoster().execute("trade", trade.id, "decline");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

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

        final TextView pText = findViewById(R.id.player_2);
        pText.setText(String.format((String) pText.getText(), partnerName));

        // Setup trade in database
        setupDatabase();

        // Popup list of pieces recyclerView
        fetchPieces();

        // Players list of pieces recyclerView
        playerTradeItems.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        playerAdapter = new PieceListAdapter(playerTradeItems, this, playerItemList);
        playerTradeItems.setAdapter(playerAdapter);

        // Partners list of pieces recyclerView
        partnerTradeItems.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        partnerAdapter = new PieceListAdapter(partnerTradeItems, this, partnerItemList);
        partnerTradeItems.setAdapter(partnerAdapter);

        // Opens the popup to look for pieces to trade
        addPieces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAddPiecePopup();
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

                    if (!getUserResult.equals("null") && !getUserResult.equals("{ }")) {

                        lastTradeState = trade;
                        trade = gson.fromJson(getUserResult, Trade.class);
                        partnerItemList.clear();
                        if (role == Role.Trader)
                            trade.partnerTradeItems.entrySet().stream().forEach(x -> processPieces(x, partnerItemList));
                        else if (role == Role.Partner)
                            trade.traderTradeItems.entrySet().stream().forEach(x -> processPieces(x, partnerItemList));

                        partnerAdapter.notifyDataSetChanged();

                        /**
                         *   Check if some players accepted status has changed as a result of
                         *   the other player changing his offered items. In that
                         *   case the addPiece Button needs to be activated again
                         *   and the selectedPiece is set to null for each player.
                         *   Accepted statuses are reset on the server side.
                         */
//                        if((lastTradeState.traderAccepted || lastTradeState.partnerAccepted) && (!trade.traderAccepted || !trade.partnerAccepted)){
//                            Toast.makeText(getBaseContext(), "This seems to work", Toast.LENGTH_SHORT).show();
//                        }

                        if ((role == Role.Trader && lastTradeState.traderAccepted && !trade.traderAccepted) ||
                                (role == Role.Partner && lastTradeState.partnerAccepted && !trade.partnerAccepted)) {

                            addPieces.setEnabled(true);
                            acceptTrade.setEnabled(true);
                            selectedPiece = null;

                            Toast.makeText(getBaseContext(), String.format("%s has changed his offered pieces, your Accept state has been reversed.", partnerName), Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        // Check if last trade was successful
                        HTTPGetter get = new HTTPGetter();
                        get.execute("trade", trade.getId(), "getLastTrade");
                        getUserResult = get.get();

                        if(!getUserResult.equals("null") && !getUserResult.equals("{ }")){

                            LastTrade lastTrade = gson.fromJson(getUserResult, LastTrade.class);

                            Calendar calendar = Calendar.getInstance();
                            int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
                            int year = calendar.get(Calendar.YEAR);

                            if(lastTrade.dayOfYear == dayOfYear && lastTrade.year == year){
                                if(lastTrade.playerOne.equals(FirebaseAuth.getInstance().getUid()) || lastTrade.playerTwo.equals(FirebaseAuth.getInstance().getUid()))
                                    createTradeSuccessfulPopup(lastTrade.traderAccepted, lastTrade.playerAccepted);

                            }
                            else{
                                Toast.makeText(getBaseContext(), String.format("Trade canceled by %s.", partnerName), Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                        else{
                            Toast.makeText(getBaseContext(), String.format("Trade canceled by %s.", partnerName), Toast.LENGTH_LONG).show();
                            finish();
                        }
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
                    // "Trade Done!"
                    // "FAIL"

                    if(playerItemList.isEmpty()){
                        Toast.makeText(getBaseContext(), "You have to offer at least one piece before accepting!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(selectedPiece == null){
                        Toast.makeText(getBaseContext(), "Select a piece from your partner before you accept the trade.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    HTTPGetter getter = new HTTPGetter();
                    getter.execute("trade", FirebaseAuth.getInstance().getUid(), "getOpenTrade");
                    String getUserResult = getter.get();

                    if (getUserResult.equals("null") || getUserResult.equals("{ }")) {
                        Toast.makeText(getBaseContext(), String.format("Trade canceled by %s.", partnerName), Toast.LENGTH_LONG).show();
                        finish();
                    }
                    trade = gson.fromJson(getUserResult, Trade.class);

                    // Disable button to select pieces
                    addPieces.setEnabled(false);
                    acceptTrade.setEnabled(false);

                    HTTPPoster poster = new HTTPPoster();
                    if(role == Role.Trader){
                        trade.traderAccepted = true;
                        trade.traderOffer = new Offer();
                        trade.traderOffer.setId = selectedPiece.getSetId();
                        trade.traderOffer.x = selectedPiece.getHorizontalPosition();
                        trade.traderOffer.y = selectedPiece.getVerticalPosition();

                        poster.execute(
                                "trade",
                                trade.getId(),
                                FirebaseAuth.getInstance().getUid(),
                                Uri.encode(gson.toJson(trade.traderOffer, Offer.class)),
                                "accept");
                    }
                    else if(role == Role.Partner) {
                        trade.partnerAccepted = true;
                        trade.partnerOffer = new Offer();
                        trade.partnerOffer.setId = selectedPiece.getSetId();
                        trade.partnerOffer.x = selectedPiece.getHorizontalPosition();
                        trade.partnerOffer.y = selectedPiece.getVerticalPosition();

                        poster.execute(
                                "trade",
                                trade.getId(),
                                FirebaseAuth.getInstance().getUid(),
                                Uri.encode(gson.toJson(trade.partnerOffer, Offer.class)),
                                "accept");
                    }

                    String result = poster.get();

                    if(result.equals("Trade Done!"))
                            createTradeSuccessfulPopup(trade.partnerOffer, trade.traderOffer);
                    else
                        trade = gson.fromJson(result, Trade.class);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        // Decline the trade, leave the activity
        declineTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void addPieceToTradeView(int bindingAdapterPosition) {
        playerItemList.add(popUpItemList.remove(bindingAdapterPosition));
        playerAdapter.notifyDataSetChanged();
        popUpAdapter.notifyDataSetChanged();

        if (popUpItemList.isEmpty()){
            updateDatabase();
            dialog.dismiss();
        }
    }

    public void removePieceFromTradeView(int bindingAdapterPosition){
        if((role == Role.Trader && trade.traderAccepted) || (role == Role.Partner && trade.partnerAccepted)){
            Toast.makeText(this, "You accepted the trade, you can't change your offers anymore.", Toast.LENGTH_LONG).show();
            return;
        }

        popUpItemList.add(playerItemList.remove(bindingAdapterPosition));
        updateDatabase();
        playerAdapter.notifyDataSetChanged();
        popUpAdapter.notifyDataSetChanged();
    }

    public void choosePieceFromPartner(int bindingAdapterPosition){
        if((role == Role.Trader && trade.traderAccepted) || (role == Role.Partner && trade.partnerAccepted)) {
            Toast.makeText(this, "You accepted the trade, you can't change your choice anymore.", Toast.LENGTH_LONG).show();
            return;
        }

        selectedPiece = partnerItemList.get(bindingAdapterPosition);
        Toast.makeText(this, String.format("You chose the pieces at position %d, click 'Accept' to lock your choice.", bindingAdapterPosition), Toast.LENGTH_SHORT).show();
    }

    private void setupDatabase(){
        try {
            // Check if trade exists
            HTTPGetter get = new HTTPGetter();
            get.execute("trade", FirebaseAuth.getInstance().getUid(), "getOpenTrade");
            String getUserResult = get.get();

            /*
             *  If the partner already created the trade it can just be pulled from the database.
             *  Otherwise the trade needs to be created first
             */
            if(!getUserResult.equals("null") && !getUserResult.equals("{ }")){
                trade = gson.fromJson(getUserResult, Trade.class);
                role = Role.Partner;
            }
            else{
                HTTPPoster poster = new HTTPPoster();
                poster.execute(
                        "trade",
                        FirebaseAuth.getInstance().getUid(),
                        this.partnerId,
                        "beginTrade");

                getUserResult = poster.get();
                if (!getUserResult.equals("null") && !getUserResult.equals("{ }")) {

                    if(getUserResult.equals("FAIL")){
                        Toast.makeText(this, String.format("You already traded with %s today, try again tomorrow.", partnerName), Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }

                    trade = gson.fromJson(getUserResult, Trade.class);
                    role = Role.Trader;
                }
            }

            //Toast.makeText(this, "Role: " + role.toString(), Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void updateDatabase() {
        try {
            HTTPGetter getter = new HTTPGetter();
            getter.execute("trade", FirebaseAuth.getInstance().getUid(), "getOpenTrade");
            String getUserResult = getter.get();
            if (!getUserResult.equals("null") && !getUserResult.equals("{ }")) {
                trade = gson.fromJson(getUserResult, Trade.class);
            }

            HTTPPoster poster = new HTTPPoster();
            if (role == Role.Trader) {
                trade.traderTradeItems.clear();
                for (int i = 0; i < playerItemList.size(); i++) {
                    PieceViewItem item = playerItemList.get(i);
                    int[][] entry;

                    if (trade.traderTradeItems.containsKey(item.getSetId())) {
                        entry = trade.traderTradeItems.get(item.getSetId());
                    } else {
                        int dim = sets.get(item.getSetId()).length;
                        entry = new int[dim][dim];
                    }

                    entry[item.getHorizontalPosition()][item.getVerticalPosition()]++;
                    trade.traderTradeItems.put(item.getSetId(), entry);
                }

                poster.execute(
                        "trade",
                        trade.getId(),
                        FirebaseAuth.getInstance().getUid(),
                        Uri.encode(gson.toJson(trade.traderTradeItems)),
                        "offer");

            } else if (role == Role.Partner) {
                trade.partnerTradeItems.clear();
                for (int i = 0; i < playerItemList.size(); i++) {
                    PieceViewItem item = playerItemList.get(i);
                    int[][] entry;

                    if (trade.partnerTradeItems.containsKey(item.getSetId())) {
                        entry = trade.partnerTradeItems.get(item.getSetId());
                    } else {
                        int dim = sets.get(item.getSetId()).length;
                        entry = new int[dim][dim];
                    }

                    entry[item.getHorizontalPosition()][item.getVerticalPosition()]++;
                    trade.partnerTradeItems.put(item.getSetId(), entry);
                }

                poster.execute(
                        "trade",
                        trade.getId(),
                        FirebaseAuth.getInstance().getUid(),
                        Uri.encode(gson.toJson(trade.partnerTradeItems)),
                        "offer");
            }

            lastTradeState = trade;
            trade = gson.fromJson(poster.get(), Trade.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchPieces(){
        this.sets = getSets(FirebaseAuth.getInstance().getUid());
        if(sets != null)
            sets.entrySet().stream().forEach(x -> processPieces(x, popUpItemList));
    }

    private void processPieces(Map.Entry<String, int[][]> x, List<PieceViewItem> itemList) {
        int[][] arr = x.getValue();
        Puzzle puzzle = createPuzzleInstance(x.getKey(), arr.length);

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j] > 0) {
                    PuzzlePiece piece = puzzle.getPuzzlePiece(i, j);

                    // The piece is added as often as the player has it
                    for (int k = 0; k < arr[i][j]; k++) {
                        PieceViewItem item = new PieceViewItem(piece.getImage(), x.getKey(), i, j);
                        itemList.add(item);
                    }
                }
            }
        }
    }

    private void createAddPiecePopup() {
        dialogBuilder = new AlertDialog.Builder(this);

        // View
        View addPiecesPopupView = getLayoutInflater().inflate(R.layout.trade_popup, null);
        tradePopupRecyclerView = addPiecesPopupView.findViewById(R.id.add_pieces_recyclerView);
        btnClose = addPiecesPopupView.findViewById(R.id.close_button);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        tradePopupRecyclerView.setLayoutManager(gridLayoutManager);

        // Adapter
        popUpAdapter = new PieceListAdapter(tradePopupRecyclerView, this, popUpItemList);
        tradePopupRecyclerView.setAdapter(popUpAdapter);

        dialogBuilder.setView(addPiecesPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatabase();
                dialog.dismiss();
            }
        });
    }

    private void createTradeSuccessfulPopup(Offer traderOffer, Offer partnerOffer){

        // View
        View tradeSuccessful = getLayoutInflater().inflate(R.layout.trade_successful_popup, null);
        final ImageView traderImg = tradeSuccessful.findViewById(R.id.trader_piece);
        final ImageView partnerImg = tradeSuccessful.findViewById(R.id.partner_piece);
        //final TextView playerText = tradeSuccessful.findViewById(R.id.player_name_text);
        final TextView partnerText = tradeSuccessful.findViewById(R.id.partner_name_text);
        final Button button = tradeSuccessful.findViewById(R.id.close_trade_button);

        partnerText.setText(partnerName);

        Map<String, int[][]> sets = getSets(FirebaseAuth.getInstance().getUid());

        // Trader
        int traderArr[][] = sets.get(traderOffer.setId);
        Puzzle traderPuzzle = createPuzzleInstance(traderOffer.setId, traderArr.length);
        PuzzlePiece traderPiece = traderPuzzle.getPuzzlePiece(traderOffer.x, traderOffer.y);

        // Partner
        int partnerArr[][] = sets.get(partnerOffer.setId);
        Puzzle partnerPuzzle = createPuzzleInstance(partnerOffer.setId, partnerArr.length);
        PuzzlePiece partnerPiece = partnerPuzzle.getPuzzlePiece(partnerOffer.x, partnerOffer.y);

        if(role == Role.Trader){
            traderImg.setImageBitmap(traderPiece.getImage());
            partnerImg.setImageBitmap(partnerPiece.getImage());
        }
        else if(role == Role.Partner){
            traderImg.setImageBitmap(partnerPiece.getImage());
            partnerImg.setImageBitmap(traderPiece.getImage());
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialogBuilder.setView(tradeSuccessful);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private Map<String, int[][]> getSets(String userId){
        HTTPGetter getter = new HTTPGetter();
        getter.execute("inventory", FirebaseAuth.getInstance().getUid(), "getInventory");
        try {
            String getUserResult = getter.get();
            if (!getUserResult.equals("{ }")) {
                Inventory inv = gson.fromJson(getUserResult, Inventory.class);
                return inv.getSets();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Puzzle createPuzzleInstance(String setId, int setDimension){
        int imageId = getResources().getIdentifier("com.socialgaming.androidtutorial:drawable/" + setId, null, null);
        Bitmap image = BitmapFactory.decodeResource(this.getResources(), imageId);
        return new Puzzle(setId, setDimension, setDimension, image);
    }
}

enum Role {
    Unasigned,
    Trader,
    Partner
}