package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.socialgaming.androidtutorial.Adapters.PieceListAdapter;
import com.socialgaming.androidtutorial.Models.PieceViewItem;

import java.util.ArrayList;
import java.util.List;

public class TradeActivity extends AppCompatActivity {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private RecyclerView piecesView;
    private Button btnAdd;
    private Button btnCancel;

    // Player 1
    private List<PieceViewItem> playerOneItemList = new ArrayList<>();
    private PieceListAdapter playerOneAdapter;

    // Popup
    private List<PieceViewItem> popUpItemList = new ArrayList<>();
    PieceListAdapter popUpAdapter;

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
        final Button acceptTrade = findViewById(R.id.accept_trade_button);
        final Button declineTrade = findViewById(R.id.decline_trade_button);

        // Popup list of pieces recyclerView (just for testing)
        Bitmap bm = BitmapFactory.decodeResource(this.getResources(), R.drawable.img_1);
        Bitmap bm1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.meme);
        popUpItemList.add(new PieceViewItem(bm));
        popUpItemList.add(new PieceViewItem(bm1));

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
    }

    public void createNewPiecesAddingDialog(){
        dialogBuilder = new AlertDialog.Builder(this);

        // View
        View addPiecesPopupView = getLayoutInflater().inflate(R.layout.trade_popup, null);
        piecesView = addPiecesPopupView.findViewById(R.id.add_pieces_recyclerView);
        btnAdd = addPiecesPopupView.findViewById(R.id.add_button);
        btnCancel = addPiecesPopupView.findViewById(R.id.cancel_button);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        piecesView.setLayoutManager(gridLayoutManager);

        // Adapter
        popUpAdapter = new PieceListAdapter(piecesView, this, popUpItemList, true);
        piecesView.setAdapter(popUpAdapter);

        dialogBuilder.setView(addPiecesPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                playerOneItemList.addAll(popUpItemList);
//                playerOneAdapter.notifyDataSetChanged();
//                dialog.dismiss();
//
//                // Remove items from selection popup
//                popUpItemList.removeAll(playerOneItemList);
//                popUpAdapter.notifyDataSetChanged();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void addPieceToTradeView(int bindingAdapterPosition){
        playerOneItemList.add(popUpItemList.remove(bindingAdapterPosition));
        playerOneAdapter.notifyDataSetChanged();
        popUpAdapter.notifyDataSetChanged();
    }

    public void removePieceFromTradeView(int bindingAdapterPosition){
        popUpItemList.add(playerOneItemList.remove(bindingAdapterPosition));
        playerOneAdapter.notifyDataSetChanged();
        popUpAdapter.notifyDataSetChanged();
    }
}
