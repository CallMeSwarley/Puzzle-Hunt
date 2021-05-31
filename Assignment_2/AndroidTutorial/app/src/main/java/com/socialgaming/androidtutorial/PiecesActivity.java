package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;

import com.socialgaming.androidtutorial.Adapters.PieceListAdapter;
import com.socialgaming.androidtutorial.Models.PieceViewItem;
import com.socialgaming.androidtutorial.Models.PuzzlePiece;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class PiecesActivity extends AppCompatActivity {

    List<PieceViewItem> pieces = new ArrayList<>();
    PieceListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pieces);

        // Recyclerview
        final RecyclerView piecesList = findViewById(R.id.pieces_recyclerview);
        piecesList.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));

        // Adapter
        adapter = new PieceListAdapter(piecesList, this, pieces);
        piecesList.setAdapter(adapter);

        for(int i = 0; i < 20; i++){
            PieceViewItem piece = new PieceViewItem();
            pieces.add(piece);
        }
    }
}