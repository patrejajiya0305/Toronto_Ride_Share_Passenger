package com.sample.packnmovers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sample.packnmovers.model.CurrentLocation;
import com.sample.packnmovers.model.PlaceDetail;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;

public class RideDetailActivity extends AppCompatActivity {


    TextView tvDestination, tvDestance, tvPrice;
    Button btnMap, btnConfirm;
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
            Place.Field.LAT_LNG, Place.Field.RATING);
    private PlaceDetail source_info;
    private LatLng source_latlng,my_location_latlng;
    Double sourceLat, sourceLng , destinationLat, destinationLng;
    private Realm realms;

    public void readRecords(){
        realms.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                CurrentLocation location = realm.where(CurrentLocation.class).equalTo("id", "1").findFirst();
                if (location!=null) {
                    Log.e("longs",""+location.getLat());
                    Log.e("longs",""+location.getLongs());
                    sourceLat = location.getLat();
                    sourceLng = location.getLongs();
                }



            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_detail);

        tvDestination = (TextView) findViewById(R.id.tvDestination);
        tvDestance = (TextView) findViewById(R.id.tvDestance);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        btnMap = (Button) findViewById(R.id.btnMap);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);

        Realm.init(this);
        realms = Realm.getDefaultInstance();
        readRecords();

        tvDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                       // .setCountry("IN")
                        .setCountry("CA")
                        .build(RideDetailActivity.this);
                intent.putExtra("places_autocomplete_search_hint", "Text");
                startActivityForResult(intent, 2001);
            }
        });

        source_info = new PlaceDetail();
        if (!Places.isInitialized()) {
            Places.initialize(this, "AIzaSyBqQq3fiF7QIfpuwVq4ruyaXcPdNl7F--U");
        }


        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RideDetailActivity.this, MapActivity.class);
                i.putExtra("sourceLat",sourceLat);
                i.putExtra("sourceLng",sourceLng);
                i.putExtra("destinationLat",destinationLat);
                i.putExtra("destinationLog",destinationLng);
                startActivity(i);
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    public static String getDistance(double lat_a, double lng_a, double lat_b, double lng_b) {
        // earth radius is in mile
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
                + Math.cos(Math.toRadians(lat_a))
                * Math.cos(Math.toRadians(lat_b)) * Math.sin(lngDiff / 2)
                * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;
        double kmConvertion = 1.6093;
        // return new Float(distance * meterConversion).floatValue();
        return String.format("%.2f", new Float(distance * kmConvertion).floatValue()) + " km";
        // return String.format("%.2f", distance)+" m";
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 2001) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                source_latlng = place.getLatLng();
                source_info = new PlaceDetail();
                source_info.setName(place.getName());
                source_info.setAddress(place.getAddress());
                source_info.setId(place.getId());
                source_info.setLatlng(place.getLatLng());

                tvDestination.setText(place.getName());

                destinationLat = place.getLatLng().latitude;
                destinationLng = place.getLatLng().longitude;
                getDirection();
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=AIzaSyBqQq3fiF7QIfpuwVq4ruyaXcPdNl7F--U");
        return urlString.toString();
    }

    private void getDirection(){
        //Getting the URL
        String url = makeURL(sourceLat,sourceLng, destinationLat, destinationLng);
        Log.e("url",""+url);
        //Showing a dialog till we get the route
        final ProgressDialog loading = ProgressDialog.show(this, "Getting Data", "Please wait...", false, false);

        //Creating a string request
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("res",""+response);
                        loading.dismiss();
                        try {
                            //Parsing json
                            final JSONObject json = new JSONObject(response);
                            JSONArray routeArray = json.getJSONArray("routes");
                            JSONObject routes = routeArray.getJSONObject(0);
                            JSONArray legsArray = routes.getJSONArray("legs");
                            JSONObject routesDistance = legsArray.getJSONObject(0);
                            JSONObject distance = routesDistance.getJSONObject("distance");
                            String dis = distance.getString("text");
                            Log.e("resss",""+dis);

                            String price = removeWord(dis, "km");
                            Log.e("ressss",""+price);

                            int covertDistance;
                            try{
                                float distances = Float.parseFloat(price);
                                covertDistance = Math.round(distances);
                                tvPrice.setText(covertDistance + " Dollar");
                                tvDestance.setText(""+covertDistance +" km");
                            }catch(NumberFormatException ex){ // handle your exception
                                tvPrice.setText(price + " Dollar");
                                tvDestance.setText(""+price +" km");
                            }




                            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");


                        }
                        catch (JSONException e) {

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                    }
                });

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public static String removeWord(String string, String word)
    {
        if (string.contains(word)) {
            String tempWord = word + " ";
            string = string.replaceAll(tempWord, "");
            tempWord = " " + word;
            string = string.replaceAll(tempWord, "");
        }
        return string;
    }



}
