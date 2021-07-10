package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Models.Gift;
import com.socialgaming.androidtutorial.Models.PuzzleModel;
import com.socialgaming.androidtutorial.Util.HTTPGetter;
import com.socialgaming.androidtutorial.Util.HTTPPoster;

import java.util.concurrent.ExecutionException;

public class InventoryActivity extends AppCompatActivity {
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Inventory Activity");
        setContentView(R.layout.activity_inventory);


        final Button sets = findViewById(R.id.sets_button);
        final Button pieces = findViewById(R.id.pieces_button);

        HTTPGetter getGift = new HTTPGetter();
        getGift.execute("gifts", FirebaseAuth.getInstance().getUid(), "getGifts");
        try {
            String getGiftResult = getGift.get();
            if (!getGiftResult.equals("{ }")) {
                Gift[] gifts = gson.fromJson(getGiftResult, Gift[].class);
                if (gifts.length > 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(InventoryActivity.this).create();
                    alertDialog.setTitle("New Gifts");
                    alertDialog.setMessage("You received " + gifts.length + " new Gifts!");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Accept Gifts", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addGiftsToInventory(gifts);
                            deleteGifts(gifts);
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Decline Gifts", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteGifts(gifts);
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }



        sets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryActivity.this, SetsActivity.class);
                startActivity(intent);
            }
        });

        pieces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryActivity.this, PiecesActivity.class);
                startActivity(intent);
            }
        });
    }
    private void deleteGifts(Gift[] gifts){
        for (Gift gift : gifts) {
            HTTPPoster deleteGifts=new HTTPPoster();
            deleteGifts.execute("gifts",gift.id,"removeGift");
            try{
                deleteGifts.get();
            }catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void addGiftsToInventory(Gift[] gifts){
        for (Gift gift : gifts) {
            gift.content.forEach((k,v)->{
                for(int i=0;i<v.length;i++){
                    for(int j=0;j<v[i].length;j++){
                        if(v[i][j]>0){
                            HTTPPoster getAddPiece = new HTTPPoster();
                            getAddPiece.execute("inventory", FirebaseAuth.getInstance().getUid(), k, "" + i,
                                    "" + j, ""+v[i][j], "addPiece");
                        }
                    }
                }
            });
        }
    }
}