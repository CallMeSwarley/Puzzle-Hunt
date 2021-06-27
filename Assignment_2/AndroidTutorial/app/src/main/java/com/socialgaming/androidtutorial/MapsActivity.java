package com.socialgaming.androidtutorial;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.socialgaming.androidtutorial.Util.HTTPGetter;
import com.socialgaming.androidtutorial.Util.HTTPPoster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int MY_RESULT_FINE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
       /* LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
*/

        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_RESULT_FINE_LOCATION);
        } else {
            mMap.setMyLocationEnabled(true);
            if (mMap != null) {
                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        mMap.clear();
                        LatLng user = new LatLng(location.getLatitude(), location.getLongitude());
                        new HTTPPoster().execute(
                                "position",
                                FirebaseAuth.getInstance().getUid(),
                                "" + user.latitude,
                                "" + user.longitude,
                                "update");
                        drawAllLocation();
                    }
                });
            }
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(Build.VERSION.SDK) > 5 && keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        } else return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
        alertDialog.setTitle("Logout");
        alertDialog.setMessage("Doing this will log you out");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void drawAllLocation() {
        HTTPGetter get = new HTTPGetter();
        get.execute("position", FirebaseAuth.getInstance().getUid(), "getAllPositions");
        try {
            String getResult = get.get();
            if (!getResult.equals("{ }")) {
                JSONObject json = new JSONObject(getResult);
                JSONArray jsonLocations = json.getJSONArray("locations");

                for (int idx = 0; idx < jsonLocations.length(); ++idx) {
                    JSONObject jsonLocation = jsonLocations.getJSONObject(idx);

                    String userName = jsonLocation.getString("user");
                    String loc1 = jsonLocation.getString("loc1");
                    String loc12 = jsonLocation.getString("loc12");
                    String loc2 = jsonLocation.getString("loc2");
                    String loc22 = jsonLocation.getString("loc22");
                    String loc3 = jsonLocation.getString("loc3");
                    String loc32 = jsonLocation.getString("loc32");
                    Marker marker;
                    if (!loc3.equals(" ")) {
                        LatLng location3 = new LatLng(Double.parseDouble(loc3), Double.parseDouble(loc32));
                        marker = mMap.addMarker(new MarkerOptions().position(location3).title("Third last position of " + userName));
                    }
                    if (!loc2.equals(" ")) {
                        LatLng location2 = new LatLng(Double.parseDouble(loc2), Double.parseDouble(loc22));
                        marker = mMap.addMarker(new MarkerOptions().position(location2).title("Second last position of " + userName));
                    }
                    if (!loc1.equals(" ")) {
                        LatLng location1 = new LatLng(Double.parseDouble(loc1), Double.parseDouble(loc12));
                        marker = mMap.addMarker(new MarkerOptions().position(location1).title("Last position of " + userName));
                        //  mMap.moveCamera(CameraUpdateFactory.newLatLng(location1));
                    }
                }
            } else {
                System.out.println("empty");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}