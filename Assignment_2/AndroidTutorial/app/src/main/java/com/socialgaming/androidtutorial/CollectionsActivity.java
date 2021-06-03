package com.socialgaming.androidtutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.socialgaming.androidtutorial.Models.Puzzle;
import com.socialgaming.androidtutorial.Models.PuzzlePiece;

import java.util.Collections;
import java.util.List;

public class CollectionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);
        Drawable image = getResources().getDrawable(R.drawable.meme);
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

        ImageView imageView = findViewById(R.id.imageView9);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

        for(int i=0; i<puzzle.piecesCountHorizontal;i++){
            for(int j=0; j<puzzle.piecesCountVertical; j++){
                view[j][i].setImageBitmap(pieces[i][j].getImage());
                if((i==1 || i==2 ) && j==1){
                    view[j][i].setColorFilter(filter);
                }
               // view[i][j].setBackground(view[i][j].getDrawable());
            }
        }



        // Initialize the SDK
        Places.initialize(getApplicationContext(), String.valueOf(R.string.google_maps_key));
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        // Define a Place ID.
        final String placeId = "ChIJcSLH9Lx3nkcReb8j9UMv5H8";

        // Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
        final List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);

        // Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, fields);

        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            final Place place = response.getPlace();

            // Get the photo metadata.
            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.w("Puzzle", "No photo metadata.");
                return;
            }
            final PhotoMetadata photoMetadata = metadata.get(0);

            // Get the attribution text.
            final String attributions = photoMetadata.getAttributions();

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                imageView.setImageBitmap(bitmap);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e("Puzzle", "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                    // TODO: Handle error with given status code.
                }
            });
        });




    }

    public void Test() {

    }
}