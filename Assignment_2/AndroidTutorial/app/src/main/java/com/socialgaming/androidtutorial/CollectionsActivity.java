package com.socialgaming.androidtutorial;

import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.socialgaming.androidtutorial.Models.Puzzle;
import com.socialgaming.androidtutorial.Models.PuzzlePiece;

public class CollectionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);
        Drawable image = getResources().getDrawable(R.drawable.meme);
        Bitmap returnedBitmap = ((BitmapDrawable) image).getBitmap();
        Puzzle puzzle = new Puzzle("1", 3, 3, returnedBitmap);
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

        ImageView imageView = findViewById(R.id.imageView9);
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

    public void Test() {

    }
}