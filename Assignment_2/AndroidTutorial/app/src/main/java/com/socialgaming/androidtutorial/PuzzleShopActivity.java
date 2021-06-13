package com.socialgaming.androidtutorial;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Models.Inventory;
import com.socialgaming.androidtutorial.Models.PieceViewItem;
import com.socialgaming.androidtutorial.Models.Puzzle;
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
        //TODO Entfernungscheck(20 m) zum abrufen des Shops einbauen
        Inventory inventory;
        //Liste der Pieces die man schon hat
        ArrayList<PuzzlePiece> myPuzzlePieces = new ArrayList<>();
        //Lister der Pieces die es gibt
        ArrayList<PuzzlePiece> allPuzzlePieces = new ArrayList<>();
        HTTPGetter get = new HTTPGetter();
        get.execute("inventory", FirebaseAuth.getInstance().getUid(), "getInventory");
        try {
            String getInventoryResult = get.get();
            if (!getInventoryResult.equals("{ }")) {
                inventory = gson.fromJson(getInventoryResult, Inventory.class);
                //TODO Liste mit allen pieces initialisieren
                //Code aus der InventoryActivity übernommen
                inventory.sets.entrySet().stream().forEach(x -> {
                    int[][] arr = x.getValue();
                    int imageId = getResources().getIdentifier("com.socialgaming.androidtutorial:drawable/" + x.getKey(), null, null);
                    Bitmap image = BitmapFactory.decodeResource(this.getResources(), imageId);
                    Puzzle puzzle = new Puzzle(x.getKey(), arr.length, arr.length, image);
                    for (int i = 0; i < arr.length; i++) {
                        for (int j = 0; j < arr[i].length; j++) {
                            if (arr[i][j] > 0) {
                                PuzzlePiece piece = puzzle.getPuzzlePiece(i, j);
                                if(!myPuzzlePieces.contains(piece)){
                                    myPuzzlePieces.add(piece);
                                }
                            }
                        }
                    }
                });
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

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
                                HTTPGetter getAddPiece = new HTTPGetter();
                                getAddPiece.execute("inventory", FirebaseAuth.getInstance().getUid(), puzzlePiece.getPuzzleParent().id, "" + puzzlePiece.getPositionHorizontal(),
                                        "" + puzzlePiece.getPositionVertical(), "" + 1, "addPiece");
                                try {
                                    String getInventoryResult = get.get();
                                    if (!getInventoryResult.equals("{ }")) {

                                    }
                                } catch (ExecutionException | InterruptedException e) {
                                    e.printStackTrace();
                                }
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(Build.VERSION.SDK) > 5 && keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        } else return super.onKeyDown(keyCode, event);
    }

    //Wenn man den shop verlässt dann wird er beim nächsen mal mit neuen teilen geladen
    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(PuzzleShopActivity.this).create();
        alertDialog.setTitle("Back to Menu");
        alertDialog.setMessage("Doing this will reload the Shop");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Back to Mainmenu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(PuzzleShopActivity.this, MainMenuActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Stay in Shop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}