package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;

import com.socialgaming.androidtutorial.Models.PuzzlePiece;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class PiecesActivity extends AppCompatActivity {

    List<PuzzlePiece> pieces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pieces);

        pieces = new ArrayList<>();

        for(int i = 0; i < 20; i++){



            //Piece piece = new Piece("0000", "MUC", );

        }

    }
}