package com.socialgaming.androidtutorial;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Models.Inventory;
import com.socialgaming.androidtutorial.Models.Puzzle;
import com.socialgaming.androidtutorial.Models.PuzzleModel;
import com.socialgaming.androidtutorial.Models.PuzzlePiece;
import com.socialgaming.androidtutorial.Models.User;
import com.socialgaming.androidtutorial.Util.HTTPGetter;
import com.socialgaming.androidtutorial.Util.HTTPPoster;

import java.util.ArrayList;
import java.util.Arrays;
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

        TextView xpAnzeige = findViewById(R.id.xpDisplayShop);
        HTTPGetter getMe = new HTTPGetter();
        getMe.execute("user", FirebaseAuth.getInstance().getUid(), "getUser");
        try {
            String getUserResult = getMe.get();
            if (!getUserResult.equals("{ }")) {
                User user = gson.fromJson(getUserResult, User.class);
                String xpString = "You have " + user.xp + " XP";
                xpAnzeige.setText(xpString);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
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
                                if (!myPuzzlePieces.contains(piece)) {
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
        PuzzleModel[] allPuzzles = new PuzzleModel[0];
        HTTPGetter getAll = new HTTPGetter();
        getAll.execute("puzzle", "getAll");
        try {
            String getAllPuzzleResult = getAll.get();
            if (!getAllPuzzleResult.equals("{ }")) {
                allPuzzles = gson.fromJson(getAllPuzzleResult, PuzzleModel[].class);
                for (PuzzleModel puzzleModel : allPuzzles) {
                    int imageId = getResources().getIdentifier("com.socialgaming.androidtutorial:drawable/" + puzzleModel.id, null, null);
                    Bitmap image = BitmapFactory.decodeResource(this.getResources(), imageId);
                    Puzzle puzzle = new Puzzle(puzzleModel.id, puzzleModel.piecesCountHorizontal, puzzleModel.piecesCountVertical, image);
                    PuzzlePiece[][] pieces2D = puzzle.getAllPuzzlePieces();
                    for (PuzzlePiece[] arr : pieces2D) {
                        allPuzzlePieces.addAll(Arrays.asList(arr));
                    }
                }
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
                XP = 1500;
            else if (i < 5)
                XP = 3000;
            else XP = 5000;
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
            ViewGroup.LayoutParams params = imageButton.getLayoutParams();
            params.height = 300;
            params.width = 300;
            imageButton.setLayoutParams(params);
            //Button funktionalität geben
            imageButton.setOnClickListener(v -> {
                AlertDialog alertDialog = new AlertDialog.Builder(PuzzleShopActivity.this).create();
                alertDialog.setTitle("Buy Piece");
                alertDialog.setMessage("Buy Puzzle Piece for" + XP + " XP?");
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
                                if (user.xp < XP) {
                                    Toast.makeText(PuzzleShopActivity.this, "You don't have enough XP for that piece!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    user.xp -= XP;
                                    String xpString = "You have " + user.xp + " XP";
                                    xpAnzeige.setText(xpString);
                                    user.xp -= 100;
                                    new HTTPPoster().execute(
                                            "user",
                                            Uri.encode(gson.toJson(user)),//necessary to escape "unsafe" characters, otherwise error in play framework
                                            "update");
                                    allPuzzlePieces.remove(puzzlePiece);
                                    myPuzzlePieces.add(puzzlePiece);
                                    HTTPPoster postAddPiece = new HTTPPoster();
                                    postAddPiece.execute("inventory",
                                            FirebaseAuth.getInstance().getUid(),
                                            puzzlePiece.getPuzzleParent().id,
                                            "" + puzzlePiece.getPositionHorizontal(),
                                            "" + puzzlePiece.getPositionVertical(),
                                            "1",
                                            "addPiece");
                                    //make button Unclickable and grayish
                                    imageButton.setEnabled(false);
                                    Drawable icon = convertDrawableToGrayScale(new BitmapDrawable(getResources(), puzzlePiece.getImage()));
                                    imageButton.setImageDrawable(icon);
                                    Toast.makeText(PuzzleShopActivity.this, "Bought Piece for " + XP + " XP!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
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
        final ColorMatrix grayscaleMatrix = new ColorMatrix();
        grayscaleMatrix.setSaturation(0);
        res.setColorFilter(new ColorMatrixColorFilter(grayscaleMatrix));
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
        alertDialog.setTitle("Return to Map");
        alertDialog.setMessage("Doing this will reload the Shop");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Back to Map", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(PuzzleShopActivity.this, PuzzleMapActivity.class);
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