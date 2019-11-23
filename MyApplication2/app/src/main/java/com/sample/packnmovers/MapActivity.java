package com.sample.packnmovers;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sample.packnmovers.model.PlaceDetail;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;




public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private PlaceDetail source_info, destination_info;

    Polyline line;
    private GoogleMap google_map;
    private Location my_location;
    double sL11,sL12,dL11,dL12;
    private LatLng source_latlng, destination_latlng;
    // Set the fields to specify which types of place data to return.
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
            Place.Field.LAT_LNG, Place.Field.RATING);
    private FusedLocationProviderClient fusedLocationClient;

    //JobDetails jobDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setContentView(R.layout.activity_map);
        if (getIntent() == null || getIntent().getExtras() == null) {
            Toast.makeText(MapActivity.this,
                    "Unable to load, Please try again.", Toast.LENGTH_LONG).show();
            return;
        }
      //  jobDetails = (JobDetails) getIntent().getSerializableExtra("extras_job_details");
      //  Log.e("Tab 3", jobDetails.getDate() + " Time : " + jobDetails.getTime());





        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
              //  newString= null;
            } else {
                 sL11= extras.getDouble("sourceLat");
                sL12= extras.getDouble("sourceLng");
                dL11= extras.getDouble("destinationLat");
                dL12= extras.getDouble("destinationLog");
            }
        } else {
            //newString= (String) savedInstanceState.getSerializable("STRING_I_NEED");
            sL11= (double)savedInstanceState.getSerializable("sourceLat");
            sL12= (double)savedInstanceState.getSerializable("sourceLng");
            dL11= (double)savedInstanceState.getSerializable("destinationLat");
            dL12= (double)savedInstanceState.getSerializable("destinationLog");
        }




        // sL11 = Double.parseDouble(jobDetails.getSourceLatt());
       //  sL12 = Double.parseDouble(jobDetails.getSourceLong());
        source_latlng = new LatLng(sL11, sL12);

       //  dL11 = Double.parseDouble(jobDetails.getDestLatt());
      //   dL12 = Double.parseDouble(jobDetails.getDestLong());
        destination_latlng = new LatLng(dL11, dL12);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_to_go);
        mapFragment.getMapAsync(this);

      //  source_info = new PlaceInfo();
      //  destination_info = new PlaceInfo();

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBqQq3fiF7QIfpuwVq4ruyaXcPdNl7F--U");
        }

        String urlTopass = makeURL(source_latlng.latitude,
                source_latlng.longitude, destination_latlng.latitude,
                destination_latlng.longitude);
        new connectAsyncTask(urlTopass).execute();
    }


    public void getCurrentAddress() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(MapActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    source_latlng = new LatLng(location.getLatitude(), location.getLongitude());
                    //currentAddress(location.getLatitude(), location.getLongitude());
                }
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        if (!isLocationEnabled(MapActivity.this)) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(MapActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(MapActivity.this);
            }
            builder.setTitle("Lcoation Services Disabled")
                    .setMessage("Please Turn Your GPS Location On!")
                    .show();
            if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Please give app permissions in Settings for the app to work!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        google_map = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(MapActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // currentAddress(location.getLatitude(), location.getLongitude());
                        my_location = location;

                        // my_location = gpsTracker.getLocation();
                        //my_location_latlng = new LatLng(my_location.getLatitude(), my_location.getLongitude());

                        google_map.setMinZoomPreference(10.0f);
                        google_map.moveCamera(CameraUpdateFactory.newLatLngZoom(source_latlng, 5));

                    }
                }
            });
        }
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }


    /**
     * Override the activity's onActivityResult(), check the request code, and
     * do something with the returned place data (in this sample it's place name and place ID).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2000) {
            if (resultCode == RESULT_OK) {
                com.google.android.libraries.places.api.model.Place place = Autocomplete.getPlaceFromIntent(data);
                destination_latlng = place.getLatLng();
                destination_info = new PlaceDetail();
                destination_info.setName(place.getName().toString());
                destination_info.setAddress(place.getAddress().toString());
                destination_info.setId(place.getId());
                destination_info.setLatlng(place.getLatLng());


                //  setmarker.set_marker(source_info, destination_info, my_location_latlng, 1, google_map);
                    /*if (!tv_from_auto_complete.getText().toString().equals("")) {
                        String urlTopass = makeURL(source_latlng.latitude,
                                source_latlng.longitude, destination_latlng.latitude,
                                destination_latlng.longitude);
                        new connectAsyncTask(urlTopass).execute();
                    }*/

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        if (requestCode == 2001) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                source_latlng = place.getLatLng();
                source_info = new PlaceDetail();
                source_info.setName(place.getName().toString());
                source_info.setAddress(place.getAddress().toString());
                source_info.setId(place.getId());
                source_info.setLatlng(place.getLatLng());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private class connectAsyncTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        String url;

        connectAsyncTask(String urlPass) {
            url = urlPass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MapActivity.this);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            String json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            if (result != null) {
                drawPath(result);
            }
        }
    }

    public String makeURL(double sourcelat, double sourcelog, double destlat,
                          double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true&key=AIzaSyBqQq3fiF7QIfpuwVq4ruyaXcPdNl7F--U");
        return urlString.toString();
    }

    public class JSONParser {
        InputStream is = null;
        JSONObject jObj = null;
        String json = "";

        public JSONParser() {
        }

        public String getJSONFromUrl(String url) {
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                json = sb.toString();
                is.close();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
            return json;
        }
    }

    public void drawPath(String result) {
        if (line != null) {
            google_map.clear();
        }
        google_map.addMarker(new MarkerOptions()
                .position(source_latlng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .title("Source: " /* jobDetails.getLoginUserProfile().getName()*/));
        google_map.addMarker(new MarkerOptions()
                .position(destination_latlng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("Destination"));
        try {
            // Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes
                    .getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            for (int z = 0; z < list.size() - 1; z++) {
                LatLng src = list.get(z);
                LatLng dest = list.get(z + 1);
                line = google_map.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude),
                                new LatLng(dest.latitude, dest.longitude))
                        .width(10).color(getResources().getColor(R.color.color2196F3)).geodesic(true));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        google_map.moveCamera(CameraUpdateFactory.newLatLngZoom(source_latlng, 10));
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


    public boolean getCompleteAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(
                this, Locale
                .getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude,
                    latLng.longitude, 1);
            if (addresses.get(0).getLocality() != null) {
                if (addresses.get(0).getLocality().equals("Indore")) {
                    return true;
                } else if (addresses.get(0).getLocality().equals("Ahmedabad")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }
}