package com.socialgaming.androidtutorial;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.socialgaming.androidtutorial.Models.Dealer;
import com.socialgaming.androidtutorial.Models.Location;
import com.socialgaming.androidtutorial.Models.Markers;
import com.socialgaming.androidtutorial.Models.Puzzle;
import com.socialgaming.androidtutorial.Models.PuzzleModel;
import com.socialgaming.androidtutorial.Models.PuzzlePiece;
import com.socialgaming.androidtutorial.Models.Shop;
import com.socialgaming.androidtutorial.Models.Trade;
import com.socialgaming.androidtutorial.Models.Weather;
import com.socialgaming.androidtutorial.Util.HTTPGetter;
import com.socialgaming.androidtutorial.Util.HTTPPoster;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

//Weather implementation idee: https://github.com/survivingwithandroid/Swa-app/blob/master/WeatherApp/src/com/survivingwithandroid/weatherapp/MainActivity.java,
//wurde angepasst

public class PuzzleMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final double DISTANCE_THRESHOLD = 100;
    private GoogleMap mMap;

    int MY_RESULT_FINE_LOCATION;
    private static String url = "http://api.openweathermap.org/data/2.5/weather?";
    private static String imgUrl = "http://openweathermap.org/img/wn/";
    private static String appid = "858fcdc021157c5dd2e1cd35925ae125";
    private final Gson gson = new Gson();
    private TextView infoText;
    private TextView condDescr;
    private ImageView imgView;

    @Override
    protected void onStop() {
        super.onStop();
        this.handler.removeCallbacksAndMessages(null);
    }

    private final Handler handler = new Handler();
    private static final int DELAY_LOCATION = 5000;
    private static final int DELAY_WEATHER = 7000;
    private LocationRequest mLocationRequest;
    private android.location.Location mLastLocation;
    private Marker mCurrLocationMarker;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double distance = 0;
    private PuzzleModel[] allPuzzles = new PuzzleModel[0];
    private ArrayList<PuzzlePiece> allPuzzlePieces = new ArrayList<>();
    private String current_weather_condition = "Clear";
    private boolean focused = true;
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<android.location.Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                android.location.Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
            }
        }
    };
    private OnSuccessListener<android.location.Location> locationSuccess = new OnSuccessListener<android.location.Location>() {
        @Override
        public void onSuccess(android.location.Location location) {
            if (focused && location != null) {
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                if (mLastLocation != null) {
                    distance += distance(mLastLocation, location);
                    System.out.println(distance);
                }
                HTTPGetter checkForTrades = new HTTPGetter();
                checkForTrades.execute("trade", FirebaseAuth.getInstance().getUid(), "getOpenTrade");
                while (distance > DISTANCE_THRESHOLD) {
                    distance -= DISTANCE_THRESHOLD;
                    AlertDialog alertDialog = new AlertDialog.Builder(PuzzleMapActivity.this).create();
                    alertDialog.setTitle("Puzzle");
                    alertDialog.setMessage("You received a Puzzle Pieces!");

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", (dialog, which) -> {
                        dialog.dismiss();
                    });
                    alertDialog.show();
                    //TODO:random puzzle piece
                    switch (current_weather_condition) {
                        case "Clear":
                            addRandomPiece();
                            break;
                        case "Rain":
                        case "Drizzle":
                            addRandomCornerPiece();
                            break;
                        case "Clouds":
                            addRandomEdgePiece();
                            break;
                        case "Snow":
                            addRandomOddPiece();
                            ;
                            break;
                        case "Thunderstorm":
                            addRandomEvenPiece();
                            break;
                        default:
                            addRandomPiece();
                            break;
                    }
                }
                mLastLocation = location;

                HTTPPoster locationUpdate = new HTTPPoster();
                locationUpdate.execute("position",
                        FirebaseAuth.getInstance().getUid(),
                        String.valueOf(location.getLatitude()),
                        String.valueOf(location.getLongitude()),
                        "update");
                JSONWeatherTask task = new JSONWeatherTask();
                task.execute(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()));
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                //move map camera
                LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                final Markers markers;
                final HashMap<String, String> idLookUp = new HashMap<>();
                Markers markers1;
                HTTPGetter getMarkers = new HTTPGetter();
                getMarkers.execute(
                        "markers",
                        FirebaseAuth.getInstance().getUid(),
                        String.valueOf(bounds.southwest.latitude),
                        String.valueOf(bounds.southwest.longitude),
                        String.valueOf(bounds.northeast.latitude),
                        String.valueOf(bounds.northeast.longitude),
                        "getInBound");
                try {
                    markers1 = gson.fromJson(getMarkers.get(), Markers.class);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    markers1 = new Markers();
                }
                markers = markers1;
                Shop[] activeShops = markers.activeShops;
                Shop[] visibleShops = markers.visibleShops;
                Dealer[] activeDealers = markers.activeDealers;
                Dealer[] visibleDealers = markers.visibleDealers;
                mMap.clear();
                for (Shop s : activeShops) {
                    Marker mark = mMap.addMarker(new MarkerOptions().position(new LatLng(s.lat, s.lon)).title(s.title + "\nActive").
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    mark.setTag("AS");//=ActiveShop
                }
                for (Shop s : visibleShops) {
                    Marker mark = mMap.addMarker(new MarkerOptions().position(new LatLng(s.lat, s.lon)).title(s.title + "\nActive").
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    mark.setTag("VS");//=VisibleShop
                }
                for (Dealer d : activeDealers) {
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(new LatLng(d.lat, d.lon));
                    marker.title(d.title + "\nActive");
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    Marker mark = mMap.addMarker(marker);
                    mark.setTag("AD");//=ActiveDealer
                }
                for (Dealer d : visibleDealers) {
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(new LatLng(d.lat, d.lon));
                    marker.title(d.title);
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    Marker mark = mMap.addMarker(marker);
                    mark.setTag("VD");//=VisibleDealer
                }
                for (String id : markers.nearbyUsers.keySet()) {
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(new LatLng(markers.userLocations.get(id)[0], markers.userLocations.get(id)[1]));
                    marker.title(markers.nearbyUsers.get(id).nickName);
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    Marker mark = mMap.addMarker(marker);
                    mark.setTag("NU");//=NearbyUser
                    idLookUp.put(mark.getId(), id);
                }
                mMap.setOnMarkerClickListener(marker -> {
                    System.out.println("++++++++++++++++++++++Marker click+++++++++++++++++++++++++++++++++");
                    System.out.println("++++++++++++++++" + marker.getTag() + "++++++++++++++++++++++++++++++++");

                    if (marker.getTag() != null && ((String) marker.getTag()).equals("AS")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(PuzzleMapActivity.this).create();
                        alertDialog.setTitle("Shopping");
                        alertDialog.setMessage("Do you want to enter the shop?");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", (dialog, which) -> {
                            Intent intent = new Intent(PuzzleMapActivity.this, PuzzleShopActivity.class);
                            startActivity(intent);
                            dialog.dismiss();
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", (dialog, which) -> dialog.dismiss());
                        alertDialog.show();
                        return true;
                    } else if (marker.getTag() != null && ((String) marker.getTag()).equals("AD")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(PuzzleMapActivity.this).create();
                        alertDialog.setTitle("Dealer");
                        alertDialog.setMessage("Do you wanna play a game?");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", (dialog, which) -> {
                            Intent intent = new Intent(PuzzleMapActivity.this, DealerActivity.class);
                            startActivity(intent);
                            dialog.dismiss();
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", (dialog, which) -> dialog.dismiss());
                        alertDialog.show();
                        return true;
                    } else if (marker.getTag() != null && ((String) marker.getTag()).equals("NU")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(PuzzleMapActivity.this).create();
                        alertDialog.setTitle("Trade");
                        alertDialog.setMessage("Do you wanna trade with player " + marker.getTitle() + "?");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", (dialog, which) -> {
                            Intent intent = new Intent(PuzzleMapActivity.this, TradeActivity.class);
                            TradeActivity.partnerId = idLookUp.get(marker.getId());
                            TradeActivity.partnerXp = markers.nearbyUsers.get(idLookUp.get(marker.getId())).xp;
                            TradeActivity.partnerName = markers.nearbyUsers.get(idLookUp.get(marker.getId())).nickName;
                            startActivity(intent);
                            dialog.dismiss();
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", (dialog, which) -> dialog.dismiss());
                        alertDialog.show();
                        return true;
                    }
                    return false;
                });
                //TODO add openTrade Check
                if (focused) {
                    final Trade openTrade;
                    Trade openTrade1;
                    try {
                        openTrade1 = gson.fromJson(checkForTrades.get(), Trade.class);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                        openTrade1 = null;
                    }
                    openTrade = openTrade1;
                    if (openTrade != null && markers.nearbyUsers.get(openTrade.traderId) != null) {
                        AlertDialog alertDialog = new AlertDialog.Builder(PuzzleMapActivity.this).create();
                        alertDialog.setTitle("Trade request");
                        alertDialog.setMessage("Do you wanna trade with player " + markers.nearbyUsers.get(openTrade.traderId).nickName + "?");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", (dialog, which) -> {
                            Intent intent = new Intent(PuzzleMapActivity.this, TradeActivity.class);
                            TradeActivity.partnerId = openTrade.traderId;
                            TradeActivity.partnerXp = markers.nearbyUsers.get(openTrade.traderId).xp;
                            TradeActivity.partnerName = markers.nearbyUsers.get(openTrade.traderId).nickName;
                            startActivity(intent);
                            dialog.dismiss();
                        });

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", (dialog, which) -> dialog.dismiss());
                        alertDialog.show();
                        final Runnable autoDismiss = () -> {
                            if (alertDialog.isShowing())
                                alertDialog.dismiss();
                        };
                        alertDialog.setOnDismissListener(dialog -> {
                            handler.removeCallbacks(autoDismiss);
                        });
                        handler.postDelayed(autoDismiss, DELAY_LOCATION - 1000);
                    }
                }
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        focused = false;
        //stop location updates when Activity is no longer active
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //kann mit city oder lat,lon aufgerufen werden (LIMIT 60 mal/h
        //String city = "Munich,DE";
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

        infoText = findViewById(R.id.infoText);
        condDescr = findViewById(R.id.condDescr);
        imgView = findViewById(R.id.condIcon);
        //task.execute(new String[]{city})

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PuzzleMapActivity.this, MainMenuActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(new Runnable() {
            public void run() {
                System.out.println("Location Handler"); // Do your work here
                if (ContextCompat.checkSelfPermission(PuzzleMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PuzzleMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_RESULT_FINE_LOCATION);
                }
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(PuzzleMapActivity.this, locationSuccess);
                handler.postDelayed(this, PuzzleMapActivity.DELAY_LOCATION);
            }
        }, PuzzleMapActivity.DELAY_LOCATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        focused = true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.err.println("Destroyed!!!");
        this.handler.removeCallbacksAndMessages(null);
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
        if (ContextCompat.checkSelfPermission(PuzzleMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PuzzleMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_RESULT_FINE_LOCATION);
        } else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(PuzzleMapActivity.this, locationSuccess);
            handler.postDelayed(new Runnable() {
                public void run() {
                    System.out.println("Location Handler"); // Do your work here
                    if (ContextCompat.checkSelfPermission(PuzzleMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(PuzzleMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_RESULT_FINE_LOCATION);
                    }
                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(PuzzleMapActivity.this, locationSuccess);
                    handler.postDelayed(this, PuzzleMapActivity.DELAY_LOCATION);
                }
            }, PuzzleMapActivity.DELAY_LOCATION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ContextCompat.checkSelfPermission(PuzzleMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(PuzzleMapActivity.this, "Permission has been granted! Map will work on next try", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(PuzzleMapActivity.this, "Permission has been denied! Without it the map will not work", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(PuzzleMapActivity.this, MainMenuActivity.class);
        startActivity(intent);

    }

    public String getWeatherDataWithCity(String location) {
        HttpURLConnection con = null;
        InputStream is = null;

        try {
            con = (HttpURLConnection) (new URL(url + "q=" + location + "&appid=" + appid)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Let's read the response
            StringBuilder buffer = new StringBuilder();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null)
                buffer.append(line).append("\r\n");

            is.close();
            con.disconnect();
            return buffer.toString();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable ignored) {
            }
            try {
                con.disconnect();
            } catch (Throwable ignored) {
            }
        }

        return null;

    }

    public String getWeatherDataWithLatAndLon(String lat, String lon) {
        HttpURLConnection con = null;
        InputStream is = null;

        try {
            con = (HttpURLConnection) (new URL(url + "lat=" + lat + "&lon=" + lon + "&appid=" + appid)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Let's read the response
            StringBuilder buffer = new StringBuilder();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null)
                buffer.append(line).append("\r\n");

            is.close();
            con.disconnect();
            return buffer.toString();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable ignored) {
            }
            try {
                con.disconnect();
            } catch (Throwable ignored) {
            }
        }

        return null;

    }

    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data;
            if (params.length > 1) {
                data = (getWeatherDataWithLatAndLon(params[0], params[1]));
            } else {
                data = (getWeatherDataWithCity(params[0]));
            }

            try {
                weather = getWeather(data);

                // Let's retrieve the icon
                Bitmap bmp = null;
                try {
                    InputStream in = new java.net.URL(imgUrl + weather.currentCondition.getIcon() + "@2x.png").openStream();
                    bmp = BitmapFactory.decodeStream(in);

                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                weather.iconData = bmp;

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;
        }

        //renders icon, city and condition
        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);
            imgView.setImageBitmap(weather.iconData);
            String info = getInfoText(weather.currentCondition.getCondition());
            current_weather_condition = weather.currentCondition.getCondition();
            //infoText.setText(weather.location.getCity() + "," + weather.location.getCountry());
            //condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
            infoText.setText(info);
            condDescr.setText(weather.currentCondition.getCondition());
        }

        //customized text for each weather condition
        private String getInfoText(String condition) {
            switch (condition) {
                case "Clear":
                    return "Enjoy the beautiful day outside!";
                case "Rain":
                case "Drizzle":
                    return "Don't get wet, stay inside!";
                case "Clouds":
                    return "Get your border together now!";
                case "Snow":
                    return "Where did all the color go?";
                case "Thunderstorm":
                    return "Stay safe and puzzle at home!";
                default:
                    return "Keep your eyes open!";
            }
            //TODO change spawn of puzzles based on weather conditions
        }

        protected Weather getWeather(String data) throws JSONException {
            Weather weather = new Weather();

            // We create out JSONObject from the data
            JSONObject jObj = new JSONObject(data);

            // We start extracting the info
            Location loc = new Location();

            JSONObject coordObj = getObject("coord", jObj);
            loc.setLatitude(getFloat("lat", coordObj));
            loc.setLongitude(getFloat("lon", coordObj));

            JSONObject sysObj = getObject("sys", jObj);
            loc.setCountry(getString("country", sysObj));
            loc.setCity(getString("name", jObj));
            weather.location = loc;

            // We get weather info (This is an array)
            JSONArray jArr = jObj.getJSONArray("weather");

            // We use only the first value
            JSONObject JSONWeather = jArr.getJSONObject(0);
            weather.currentCondition.setWeatherId(getInt("id", JSONWeather));
            weather.currentCondition.setDescr(getString("description", JSONWeather));
            weather.currentCondition.setCondition(getString("main", JSONWeather));
            weather.currentCondition.setIcon(getString("icon", JSONWeather));

            JSONObject mainObj = getObject("main", jObj);
            return weather;
        }


        private JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
            return jObj.getJSONObject(tagName);
        }

        private String getString(String tagName, JSONObject jObj) throws JSONException {
            return jObj.getString(tagName);
        }

        private float getFloat(String tagName, JSONObject jObj) throws JSONException {
            return (float) jObj.getDouble(tagName);
        }

        private int getInt(String tagName, JSONObject jObj) throws JSONException {
            return jObj.getInt(tagName);
        }

    }

    private void addRandomPiece() {

        PuzzlePiece randomPiece = allPuzzlePieces.get(new Random().nextInt(allPuzzlePieces.size()));
        String id = randomPiece.getPuzzleParent().id;
        int randomX = randomPiece.getPositionHorizontal();
        int randomY = randomPiece.getPositionVertical();
        HTTPPoster get = new HTTPPoster();
        get.execute("inventory", FirebaseAuth.getInstance().getUid(), id, Integer.toString(randomX), Integer.toString(randomY), "1", "addPiece");
    }

    private void addRandomOddPiece() {

        PuzzlePiece randomPiece = allPuzzlePieces.get(new Random().nextInt(allPuzzlePieces.size()));
        String id = randomPiece.getPuzzleParent().id;
        int randomX = randomPiece.getPositionHorizontal() % 2 == 1 ? randomPiece.getPositionHorizontal() : (randomPiece.getPositionHorizontal() + 1) % randomPiece.getPuzzleParent().piecesCountHorizontal;
        int randomY = randomPiece.getPositionVertical() % 2 == 1 ? randomPiece.getPositionVertical() : (randomPiece.getPositionVertical() + 1) % randomPiece.getPuzzleParent().piecesCountVertical;
        HTTPPoster get = new HTTPPoster();
        get.execute("inventory", FirebaseAuth.getInstance().getUid(), id, Integer.toString(randomX), Integer.toString(randomY), "1", "addPiece");
    }

    private void addRandomEvenPiece() {

        PuzzlePiece randomPiece = allPuzzlePieces.get(new Random().nextInt(allPuzzlePieces.size()));
        String id = randomPiece.getPuzzleParent().id;
        int randomX = randomPiece.getPositionHorizontal() % 2 == 0 ? randomPiece.getPositionHorizontal() : (randomPiece.getPositionHorizontal() + 1) % randomPiece.getPuzzleParent().piecesCountHorizontal;
        int randomY = randomPiece.getPositionVertical() % 2 == 0 ? randomPiece.getPositionVertical() : (randomPiece.getPositionVertical() + 1) % randomPiece.getPuzzleParent().piecesCountVertical;
        HTTPPoster get = new HTTPPoster();
        get.execute("inventory", FirebaseAuth.getInstance().getUid(), id, Integer.toString(randomX), Integer.toString(randomY), "1", "addPiece");
    }


    private void addRandomEdgePiece() {

        PuzzlePiece randomPiece = allPuzzlePieces.get(new Random().nextInt(allPuzzlePieces.size()));
        String id = randomPiece.getPuzzleParent().id;
        int edgeIsX = new Random().nextInt(2);
        int randomX = randomPiece.getPositionHorizontal();
        int randomY = randomPiece.getPositionVertical();
        if (edgeIsX == 0) {
            randomX = new Random().nextInt(2) == 0 ? 0 : randomPiece.getPuzzleParent().piecesCountHorizontal - 1;
        } else {
            randomY = new Random().nextInt(2) == 0 ? 0 : randomPiece.getPuzzleParent().piecesCountVertical - 1;
        }
        HTTPPoster get = new HTTPPoster();
        get.execute("inventory", FirebaseAuth.getInstance().getUid(), id, Integer.toString(randomX), Integer.toString(randomY), "1", "addPiece");
    }

    private void addRandomCornerPiece() {

        PuzzlePiece randomPiece = allPuzzlePieces.get(new Random().nextInt(allPuzzlePieces.size()));
        String id = randomPiece.getPuzzleParent().id;
        int randomX = new Random().nextInt(2) == 0 ? 0 : randomPiece.getPuzzleParent().piecesCountHorizontal - 1;
        int randomY = new Random().nextInt(2) == 0 ? 0 : randomPiece.getPuzzleParent().piecesCountVertical - 1;
        HTTPPoster get = new HTTPPoster();
        get.execute("inventory", FirebaseAuth.getInstance().getUid(), id, Integer.toString(randomX), Integer.toString(randomY), "1", "addPiece");
    }


    private double distance(android.location.Location loc1, android.location.Location loc2) {

        double radLon1 = Math.toRadians(loc1.getLongitude());
        double radLon2 = Math.toRadians(loc2.getLongitude());
        double radLat1 = Math.toRadians(loc1.getLatitude());
        double radLat2 = Math.toRadians(loc2.getLatitude());
        double dlon = radLon2 - radLon1;
        double dlat = radLat2 - radLat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double r = 6371e3;

        return (c * r);
    }

}