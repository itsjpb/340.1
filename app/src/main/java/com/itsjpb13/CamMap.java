package com.itsjpb13;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import android.location.Address;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class CamMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    String dataUrl = "https://web6.seattle.gov/Travelers/api/Map/Data?zoomId=13&type=2";
    boolean locationPermissionAccess = false;
    Location startLocation = new Location(LocationManager.GPS_PROVIDER);
    Location userLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    ArrayList<Cam> camArrayList = new ArrayList<>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        startLocation.setLatitude(47.60917268757429);
        startLocation.setLongitude(-122.34050919815874);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult){
                for (Location locale : locationResult.getLocations()){
                    if (locale != null) {
                        setLocale(locale);
                    }
                }
            }
        };



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        camArrayList = fillList(camArrayList);
        checkLocalePermission();




    }
    @SuppressLint("MissingPermission")
    private void checkLocalePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    userLocation = location;
                    setLocale(userLocation);

                }  else {
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                }
            });
        } else {
            // Permission is missing and must be requested.
            Log.d("LOCATION", "should request");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            checkLocalePermission();
        }
    }

    @SuppressLint({"MissingPermission", "MissingSuperCall"})
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length== 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        locationPermissionAccess = true;
                        userLocation = location;
                        setLocale(userLocation);

                    }  else {
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                });
            }
        } else {
            // Permission is missing and must be requested.
            Log.d("LOCATION", "should request");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);


        }

    }

    private void setLocale(Location location) {
        if (location != null) {
            mMap.setMinZoomPreference(12f);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
                    location.getLatitude(), location.getLongitude())));
            mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Your Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    );

        }
    }

    private ArrayList<Cam> fillList(ArrayList<Cam> camList) {
        RequestQueue myQ = Volley.newRequestQueue(this);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, dataUrl, null, response -> {
            try {
                JSONArray features = response.getJSONArray("Features");
                for (int i = 0; i<features.length(); i++) {
                    JSONObject locale = features.getJSONObject(i);
                    JSONArray coords = locale.getJSONArray("PointCoordinate");
                    double[] thisCoords = {coords.getDouble(0), coords.getDouble(1)};
                    JSONArray cams = locale.getJSONArray("Cameras");

                    for (int j = 0; j < cams.length(); j++){
                        JSONObject thisCam = cams.getJSONObject(j);
                        String thisUrl = thisCam.getString("ImageUrl");
                        String thisDescription = thisCam.getString("Description");
                        String thistype = thisCam.getString("Type");

                        camList.add( new Cam(thisCoords, thisDescription, thisUrl, thistype));
                        mMap.addMarker( new MarkerOptions()
                                .position(new LatLng(thisCoords[0], thisCoords[1]))
                                .title(thisDescription));
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        } , error -> Log.d("jsonError", "error: " + error.getMessage()));
        myQ.add(objectRequest);
        return camList;
    }

}