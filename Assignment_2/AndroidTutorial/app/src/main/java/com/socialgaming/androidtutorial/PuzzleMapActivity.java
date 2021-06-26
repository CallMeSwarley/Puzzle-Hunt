package com.socialgaming.androidtutorial;

import android.Manifest;
import android.content.DialogInterface;
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
import com.socialgaming.androidtutorial.Models.Shop;
import com.socialgaming.androidtutorial.Models.Weather;
import com.socialgaming.androidtutorial.Util.HTTPGetter;
import com.socialgaming.androidtutorial.Util.HTTPPoster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

//Weather implementation idee: https://github.com/survivingwithandroid/Swa-app/blob/master/WeatherApp/src/com/survivingwithandroid/weatherapp/MainActivity.java,
//wurde angepasst

public class PuzzleMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    int MY_RESULT_FINE_LOCATION;
    private static String url = "http://api.openweathermap.org/data/2.5/weather?";
    private static String imgUrl = "http://openweathermap.org/img/wn/";
    private static String appid = "858fcdc021157c5dd2e1cd35925ae125";
    private final Gson gson = new Gson();
    private TextView infoText;
    private TextView condDescr;
    private ImageView imgView;
    private final Handler handler = new Handler();
    private static final int DELAY_LOCATION = 4000;
    private static final int DELAY_WEATHER = 5000;
    private LocationRequest mLocationRequest;
    private android.location.Location mLastLocation;
    private Marker mCurrLocationMarker;
    private FusedLocationProviderClient fusedLocationProviderClient;
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

    @Override
    public void onPause() {
        super.onPause();

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
        infoText = findViewById(R.id.infoText);
        condDescr = findViewById(R.id.condDescr);
        imgView = findViewById(R.id.condIcon);
        //task.execute(new String[]{city})
        handler.postDelayed(new Runnable() {
            public void run() {
                System.out.println("Location Handler"); // Do your work here
                if (ActivityCompat.checkSelfPermission(PuzzleMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PuzzleMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(PuzzleMapActivity.this, new OnSuccessListener<android.location.Location>() {
                    @Override
                    public void onSuccess(android.location.Location location) {
                        if (location != null) {
                            Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                            mLastLocation = location;
                            if (mCurrLocationMarker != null) {
                                mCurrLocationMarker.remove();
                            }
                            //move map camera
                            LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                            Shop[] activeShops = getActiveShops(bounds);
                            Shop[] visibleShops = getVisibleShops(bounds);
                            Dealer[] activeDealers = getActiveDealers(bounds);
                            Dealer[] visibleDealers = getVisibleDealers(bounds);
                            if (activeDealers.length != 0 && activeShops.length != 0 && visibleDealers.length != 0 && visibleShops.length != 0)
                                mMap.clear();
                            for (Shop s : activeShops) {
                                Marker mark=mMap.addMarker(new MarkerOptions().position(new LatLng(s.lat, s.lon)).title(s.title + "\nActive").
                                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                mark.setTag("AS");//=ActiveShop
                            }
                            for (Shop s : visibleShops) {
                                Marker mark=mMap.addMarker(new MarkerOptions().position(new LatLng(s.lat, s.lon)).title(s.title + "\nActive").
                                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                mark.setTag("VS");//=VisibleShop
                            }
                            for (Dealer d : activeDealers) {
                                MarkerOptions marker = new MarkerOptions();
                                marker.position(new LatLng(d.lat, d.lon));
                                marker.title(d.title + "\nActive");
                                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                mMap.addMarker(marker);
                            }
                            for (Dealer d : visibleDealers) {
                                MarkerOptions marker = new MarkerOptions();
                                marker.position(new LatLng(d.lat, d.lon));
                                marker.title(d.title);
                                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                mMap.addMarker(marker);
                            }
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    System.out.println("++++++++++++++++++++++Marker click+++++++++++++++++++++++++++++++++");
                                    System.out.println("++++++++++++++++"+marker.getTag()+"++++++++++++++++++++++++++++++++");
                                    if (marker.getTag()!=null&& ((String) marker.getTag()).equals("AS")) {
                                        AlertDialog alertDialog = new AlertDialog.Builder(PuzzleMapActivity.this).create();
                                        alertDialog.setTitle("Shopping");
                                        alertDialog.setMessage("Do you want to enter the shop?");
                                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(PuzzleMapActivity.this, PuzzleShopActivity.class);
                                                startActivity(intent);
                                                dialog.dismiss();
                                            }
                                        });
                                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        alertDialog.show();
                                        return true;
                                    } else
                                        return false;
                                }
                            });
                        }
                    }
                });
                handler.postDelayed(this, PuzzleMapActivity.DELAY_LOCATION);
            }
        }, PuzzleMapActivity.DELAY_LOCATION);
        handler.postDelayed(new Runnable() {
            public void run() {
                System.out.println("Weather handler!"); // Do your work here
                JSONWeatherTask task = new JSONWeatherTask();
                task.execute(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()));
                handler.postDelayed(this, PuzzleMapActivity.DELAY_WEATHER);
            }
        }, PuzzleMapActivity.DELAY_WEATHER);
    }

    private Dealer[] getActiveDealers(LatLngBounds bounds) {
        HTTPGetter getActiveDealers = new HTTPGetter();
        getActiveDealers.execute(
                "dealer",
                FirebaseAuth.getInstance().getUid(),
                String.valueOf(bounds.southwest.latitude),
                String.valueOf(bounds.southwest.longitude),
                String.valueOf(bounds.northeast.latitude),
                String.valueOf(bounds.northeast.longitude),
                "getActive"
        );

        String activeDealers = null;
        try {
            activeDealers = getActiveDealers.get();
            System.out.println("activeDealers:\t" + activeDealers);
            return gson.fromJson(activeDealers, Dealer[].class);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return new Dealer[0];
    }

    private Dealer[] getVisibleDealers(LatLngBounds bounds) {
        HTTPGetter getVisibleDealers = new HTTPGetter();
        getVisibleDealers.execute(
                "dealer",
                FirebaseAuth.getInstance().getUid(),
                String.valueOf(bounds.southwest.latitude),
                String.valueOf(bounds.southwest.longitude),
                String.valueOf(bounds.northeast.latitude),
                String.valueOf(bounds.northeast.longitude),
                "getVisible"
        );
        try {
            String visibleDealers = getVisibleDealers.get();
            System.out.println("visibleDealers:\t" + visibleDealers);
            return gson.fromJson(visibleDealers, Dealer[].class);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Dealer[0];
    }

    private Shop[] getActiveShops(LatLngBounds bounds) {
        HTTPGetter getActiveShops = new HTTPGetter();
        getActiveShops.execute(
                "shop",
                FirebaseAuth.getInstance().getUid(),
                String.valueOf(bounds.southwest.latitude),
                String.valueOf(bounds.southwest.longitude),
                String.valueOf(bounds.northeast.latitude),
                String.valueOf(bounds.northeast.longitude),
                "getActive"
        );
        try {
            String activeShops = getActiveShops.get();
            System.out.println("activeShops:\t" + activeShops);
            return gson.fromJson(activeShops, Shop[].class);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Shop[0];
    }

    private Shop[] getVisibleShops(LatLngBounds bounds) {
        HTTPGetter getVisibleShops = new HTTPGetter();
        getVisibleShops.execute(
                "shop",
                FirebaseAuth.getInstance().getUid(),
                String.valueOf(bounds.southwest.latitude),
                String.valueOf(bounds.southwest.longitude),
                String.valueOf(bounds.northeast.latitude),
                String.valueOf(bounds.northeast.longitude),
                "getVisible"
        );
        try {
            String visibleShops = getVisibleShops.get();
            System.out.println("visibleShops:\t" + visibleShops);
            return gson.fromJson(visibleShops, Shop[].class);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Shop[0];
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

        // Add a marker in Sydney and move the camera
       /* LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
*/
        if (ContextCompat.checkSelfPermission(PuzzleMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PuzzleMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_RESULT_FINE_LOCATION);
        } else {
            mMap.setMyLocationEnabled(true);
            if (mMap != null) {
                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(android.location.Location location) {
                        LatLng user = new LatLng(location.getLatitude(), location.getLongitude());
                        new HTTPPoster().execute(
                                "position",
                                FirebaseAuth.getInstance().getUid(),
                                "" + user.latitude,
                                "" + user.longitude,
                                "update");
                        mLastLocation = location;
                    }
                });
            }
        }

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
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null)
                buffer.append(line + "\r\n");

            is.close();
            con.disconnect();
            return buffer.toString();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
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
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null)
                buffer.append(line + "\r\n");

            is.close();
            con.disconnect();
            return buffer.toString();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }

        return null;

    }

    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = "";
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
            //infoText.setText(weather.location.getCity() + "," + weather.location.getCountry());
            //condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
            infoText.setText(info);
            condDescr.setText(weather.currentCondition.getCondition());
        }

        //customized text for each weather condition
        private String getInfoText(String condition) {
            if (condition.equals("Clear")) {
                return "Enjoy the beautiful day outside!";
            } else if (condition.equals("Rain") || condition.equals("Drizzle")) {
                return "Don't get wet, stay inside!";
            } else if (condition.equals("Clouds")) {
                return "Get your border together now!";
            } else if (condition.equals("Snow")) {
                return "Where did all the color go?";
            } else if (condition.equals("Thunderstorm")) {
                return "Stay safe and puzzle at home!";
            } else {
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
            JSONObject subObj = jObj.getJSONObject(tagName);
            return subObj;
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


}