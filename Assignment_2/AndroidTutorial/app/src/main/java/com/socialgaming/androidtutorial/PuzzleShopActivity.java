package com.socialgaming.androidtutorial;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Models.Inventory;
import com.socialgaming.androidtutorial.Models.PuzzlePiece;
import com.socialgaming.androidtutorial.Models.User;
import com.socialgaming.androidtutorial.Util.HTTPGetter;
import com.socialgaming.androidtutorial.Util.HTTPPoster;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class PuzzleShopActivity extends AppCompatActivity {
    private User user;
    private final Gson gson = new Gson();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_shop);
        //Shop nur einmal täglich aktualisieren, klappt nicht man müsste den shop in der db speichern
        /*LocalDate today = java.time.LocalDate.now();
        SharedPreferences settings = getApplicationContext().getSharedPreferences("ShopDateSettings", 0);
        int dayOfYear = settings.getInt("Day", -1);
        int year = settings.getInt("Year", -1);
        long lastShopUpdate = 1;
        if (year >= 0 && dayOfYear >= 0) {
            System.out.println(lastShopUpdate);
            lastShopUpdate = ChronoUnit.DAYS.between(LocalDate.ofYearDay(year, dayOfYear), today);
        }
        if (lastShopUpdate > 0) {
            //Nur zum testen
            settings.edit().putInt("Day", today.getDayOfYear()).putInt("Year", today.getYear()).apply();
            ImageButton button = findViewById(R.id.imageButton1);
            button.setBackgroundColor(Color.RED);
            ImageButton finalButton = button;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalButton.setBackgroundColor(Color.GREEN);
                }
            });
            button = findViewById(R.id.imageButton4);
            button.setBackgroundColor(Color.BLUE);
            ImageButton finalButton1 = button;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalButton1.setBackgroundColor(Color.GREEN);
                }
            });
        }
        if (lastShopUpdate > 10) {//Hier die 10 durch 0 tauschen wenn alles geht*/
            //settings.edit().putInt("Day", today.getDayOfYear()).putInt("Year", today.getYear()).apply();
            Inventory inventory;
            //TODO Listen initialisieren
            //Liste der Pieces die man schon hat
            ArrayList<PuzzlePiece> myPuzzlePieces = new ArrayList<>();
            //Lister der Pieces die es gibt
            ArrayList<PuzzlePiece> allPuzzlePieces = new ArrayList<>();
            //Aus allen die Rausfiltern die man bereits hat um im shop unnötige Teile zu vermeiden
            allPuzzlePieces.forEach(x -> {
                if (myPuzzlePieces.contains(x)) {
                    allPuzzlePieces.remove(x);
                }
            });
            //6 "Random" elemente Auswählen die als ImageButton angezeigt werden
            Random rand = new Random();
            for (int i = 1; i < 7; i++) {
                //Puzzlepreis bestimmen
                int XP;
                if (i < 3)
                    XP = 5;
                else if (i < 5)
                    XP = 10;
                else XP = 15;
                //Puzzlepiece holen
                int randomIndex = rand.nextInt(allPuzzlePieces.size());
                PuzzlePiece puzzlePiece = allPuzzlePieces.get(randomIndex);
                //Um zu vermeiden dass zufällig ein Teil mehrfach angezeigt wird
                allPuzzlePieces.remove(randomIndex);
                //Button holen und Initialisieren
                String buttonID = "imageButton" + i;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                ImageButton imageButton = findViewById(resID);
                imageButton.setImageBitmap(puzzlePiece.getImage());
                //Button funktionalität geben
                int finalXP = XP;
                imageButton.setOnClickListener(v -> {
                    AlertDialog alertDialog = new AlertDialog.Builder(PuzzleShopActivity.this).create();
                    alertDialog.setTitle("Buy Piece");
                    alertDialog.setMessage("Buy Puzzle Piece for" + finalXP + " XP?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //User XP aktualisieren/Prüfen ob man genug hat
                            HTTPGetter get = new HTTPGetter();
                            get.execute("user", FirebaseAuth.getInstance().getUid(), "getUser");
                            try {
                                String getUserResult = get.get();
                                if (!getUserResult.equals("{ }")) {
                                    user = gson.fromJson(getUserResult, User.class);
                                    if (user.xp < finalXP) {
                                        Toast.makeText(PuzzleShopActivity.this, "You don't have enough XP for that piece!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else {
                                        user.xp -= finalXP;
                                        new HTTPPoster().execute(
                                                "user",
                                                Uri.encode(gson.toJson(user, User.class)),//necessary to escape "unsafe" characters, otherwise error in play framework
                                                "update");
                                    }
                                    allPuzzlePieces.remove(puzzlePiece);
                                    myPuzzlePieces.add(puzzlePiece);
                                    //TODO Piece dem Userinventory hinzufügen
                                }
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                            //make button Unclickable and grayish
                            imageButton.setEnabled(false);
                            Drawable icon = convertDrawableToGrayScale(new BitmapDrawable(getResources(), puzzlePiece.getImage()));
                            imageButton.setImageDrawable(icon);
                            Toast.makeText(PuzzleShopActivity.this, "Bought Piece for " + finalXP + " XP!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                });
            }
        //}
    }

    private Drawable convertDrawableToGrayScale(Drawable drawable) {
        Drawable res = drawable.mutate();
        res.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        return res;
    }
}