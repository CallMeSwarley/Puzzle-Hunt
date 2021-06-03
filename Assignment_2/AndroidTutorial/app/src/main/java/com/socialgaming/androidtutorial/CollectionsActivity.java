package com.socialgaming.androidtutorial;

import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Models.Puzzle;
import com.socialgaming.androidtutorial.Models.PuzzleModel;
import com.socialgaming.androidtutorial.Models.PuzzlePiece;
import com.socialgaming.androidtutorial.Models.User;
import com.socialgaming.androidtutorial.Util.HTTPGetter;


import java.util.concurrent.ExecutionException;

public class CollectionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);
        int id = getResources().getIdentifier("com.socialgaming.androidtutorial:drawable/" + "img_1", null, null);
        Drawable image = getResources().getDrawable(id);
        Bitmap returnedBitmap = ((BitmapDrawable)image).getBitmap();
        Puzzle puzzle = new Puzzle("1",3,3,returnedBitmap);
        PuzzlePiece[][] pieces = puzzle.getAllPuzzlePieces();

        ImageView[][] view = new ImageView[puzzle.piecesCountHorizontal][puzzle.piecesCountVertical];
        view[0][0] = findViewById(R.id.imageView1);
        view[1][0] = findViewById(R.id.imageView2);
        view[2][0] = findViewById(R.id.imageView3);
        view[0][1] = findViewById(R.id.imageView4);
        view[1][1] = findViewById(R.id.imageView5);
        view[2][1] = findViewById(R.id.imageView6);
        view[0][2] = findViewById(R.id.imageView7);
        view[1][2] = findViewById(R.id.imageView8);
        view[2][2] = findViewById(R.id.imageView9);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

        for (int i = 0; i < puzzle.piecesCountHorizontal; i++) {
            for (int j = 0; j < puzzle.piecesCountVertical; j++) {
                view[j][i].setImageBitmap(pieces[i][j].getImage());
                if ((i == 1 || i == 2) && j == 1) {
                    view[j][i].setColorFilter(filter);
                }
                // view[i][j].setBackground(view[i][j].getDrawable());
            }
        }




    }

    private Puzzle getPuzzle(String id){

        PuzzleModel puzzle = new PuzzleModel();
        Gson gson = new Gson();
        HTTPGetter get = new HTTPGetter();
        get.execute("puzzle", id, "getPuzzle");
        try {
            String getUserResult = get.get();
            if (!getUserResult.equals("{ }")) {
                puzzle = gson.fromJson(getUserResult, PuzzleModel.class);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Convert PuzzleModel to Puzzle
        int idOfImage = getResources().getIdentifier("com.socialgaming.androidtutorial:drawable/" + puzzle.id, null, null);
        Drawable image = getResources().getDrawable(idOfImage);
        Bitmap returnedBitmap = ((BitmapDrawable)image).getBitmap();
        Puzzle ret = new Puzzle(puzzle.id,puzzle.piecesCountHorizontal,puzzle.piecesCountVertical,returnedBitmap);
        return ret;
    }





}