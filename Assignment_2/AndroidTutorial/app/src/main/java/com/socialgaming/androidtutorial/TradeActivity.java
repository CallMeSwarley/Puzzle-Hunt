package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TradeActivity extends AppCompatActivity {
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
        final Button acceptTrade = findViewById(R.id.accept_trade_button);
        final Button declineTrade = findViewById(R.id.decline_trade_button);

        
    }
}