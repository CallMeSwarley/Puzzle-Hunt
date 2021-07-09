package com.socialgaming.androidtutorial;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Models.Puzzle;
import com.socialgaming.androidtutorial.Models.PuzzleModel;
import com.socialgaming.androidtutorial.Models.PuzzlePiece;
import com.socialgaming.androidtutorial.Util.HTTPGetter;

import java.util.concurrent.ExecutionException;

public class CollectionsActivity extends AppCompatActivity {

    //TODO st puzzle title and number of pieces in respective textfields
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);

        Intent intent = getIntent();
        String puzzleString = intent.getStringExtra(SetsActivity.EXTRA_ID);
        Puzzle puzzleFromString = Puzzle.convertStringToPuzzle(puzzleString);
        String puzzle_id = puzzleFromString.id;

       // int id = getResources().getIdentifier("com.socialgaming.androidtutorial:drawable/" + puzzle_id, null, null);
        Drawable image = getResources().getDrawable(Integer.valueOf(puzzle_id));
        Bitmap returnedBitmap = ((BitmapDrawable) image).getBitmap();
        Puzzle puzzle = new Puzzle(puzzle_id, puzzleFromString.piecesCountHorizontal, puzzleFromString.piecesCountVertical, returnedBitmap,puzzleFromString.ownedPuzzlePieces);
        PuzzlePiece[][] pieces = puzzle.getAllPuzzlePieces();
        TextView numberOfPieces = findViewById(R.id.number_of_pieces);
        numberOfPieces.setText(puzzle.ownedPuzzlePieces.size()+"/"+puzzle.piecesCountVertical*puzzle.piecesCountHorizontal);
        GridLayout grid = (GridLayout) findViewById(R.id.gridlayout);
        CardView card = findViewById(R.id.cardView3x3);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);


        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels-300;
        int size = width/(puzzle.piecesCountHorizontal);


        grid = setGridSize(puzzle.piecesCountHorizontal,puzzle.piecesCountVertical,grid);

        for (int i = 0; i < puzzle.piecesCountHorizontal; i++) {
            for (int j = 0; j < puzzle.piecesCountVertical; j++) {
                //view[j][i].setImageBitmap(pieces[i][j].getImage());
                ImageView view = ((ImageView)grid.getChildAt((j+1)+((puzzle.piecesCountVertical)*(i))-1));
                view.getLayoutParams().width=size;
                view.getLayoutParams().height=size;
                view.requestLayout();
                view.setImageBitmap(pieces[i][j].getImage());
                if (!puzzle.ownedPuzzlePieces.contains(i+j*puzzle.piecesCountVertical)) {
                    view.setColorFilter(filter);
                }
                // view[i][j].setBackground(view[i][j].getDrawable());
            }
        }

    }

    private GridLayout setGridSize(int column, int row, GridLayout gridLayout){
        gridLayout.removeAllViews();

        gridLayout.setColumnCount(column);
        gridLayout.setRowCount(row +1);
        for (int i = 0, c = 0, r = 0; i < row*column; i++, c++) {
            if (c == column) {
                c = 0;
                r++;
            }
            ImageView oImageView = new ImageView(this);
            oImageView.setImageResource(R.drawable.meme);

            GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            GridLayout.Spec colspan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(
                    rowSpan, colspan);
            int margin = 5;
            gridParam.setMargins(margin,margin,margin,margin);
            gridLayout.addView(oImageView, gridParam);



        }
        return gridLayout;
    }

    private Puzzle getPuzzle(String id) {

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
        Bitmap returnedBitmap = ((BitmapDrawable) image).getBitmap();
        Puzzle ret = new Puzzle(puzzle.id, puzzle.piecesCountHorizontal, puzzle.piecesCountVertical, returnedBitmap);
        return ret;
    }


}