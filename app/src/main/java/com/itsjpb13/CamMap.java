package com.itsjpb13;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class CamMap extends AppCompatActivity
        implements OnMapReadyCallback {

    private static final String TAG = "tag";
    private GoogleMap map;
    private CameraPosition cameraPosition;
    private String dataUrl =  "https://web6.seattle.gov/Travelers/api/Map/Data?zoomId=13&type=2";

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ArrayList<Cam> camList = new ArrayList<>();

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(47.609565485583204, -122.34208772705331);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // Keys for storing activity state.
    // [START maps_current_place_state_keys]
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    // [END maps_current_place_state_keys]



    // [START maps_current_place_on_create]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String dataUrl = "https://web6.seattle.gov/Travelers/api/Map/Data?zoomId=13&type=2";
        fillList(camList);
        // [START_EXCLUDE silent]
        // [START maps_current_place_on_create_save_instance_state]
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        // [END maps_current_place_on_create_save_instance_state]
        // [END_EXCLUDE]

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_cam_map);



        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        // [START maps_current_place_map_fragment]
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // [END maps_current_place_map_fragment]
        // [END_EXCLUDE]
    }
    // [END maps_current_place_on_create]

    /**
     * Saves the state of the map when the activity is paused.
     */
    // [START maps_current_place_on_save_instance_state]
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }
    // [END maps_current_place_on_save_instance_state]

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    // [START maps_current_place_on_map_ready]
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        showCameraMarkers(camList);
        // Prompt the user for permission.
        getLocationPermission();
        // [END_EXCLUDE]


        // Turn on the My Location layer and the related control on the map.
        getDeviceLocation();
        updateLocationUI();


        // Get the current location of the device and set the position of the map.




    }
    // [END maps_current_place_on_map_ready]

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    // [START maps_current_place_get_device_location]
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                @SuppressLint("MissingPermission") Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                map.addMarker(new MarkerOptions().position(new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude())).title("Your Location")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                );
                            }
                        } else {

                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.addMarker(new MarkerOptions().position(new LatLng(defaultLocation.latitude,
                                            defaultLocation.longitude)).title("Your Location")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            map.getUiSettings().setMyLocationButtonEnabled(false);

                        }
                    }
                });

            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
    // [END maps_current_place_get_device_location]

    /**
     * Prompts the user for permission to use the device location.
     */
    // [START maps_current_place_location_permission]
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    // [END maps_current_place_location_permission]

    /**
     * Handles the result of the request for location permissions.
     */
    // [START maps_current_place_on_request_permissions_result]
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }

    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private ArrayList<Cam> fillList(ArrayList<Cam> camsList) {
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

                        camsList.add( new Cam(thisCoords, thisDescription, thisUrl, thistype));
                        Log.d(TAG, "fillList: " + String.valueOf(camsList.size()));

                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        } , error -> Log.d("jsonError", "error: " + error.getMessage()));
        myQ.add(objectRequest);
        return camsList;
    }

    private void showCameraMarkers(@NonNull ArrayList<Cam> cameraList) {
        Log.d("LOCATION", "Show camera markers");
        Log.d("ADD", String.valueOf(cameraList.size()));
        for (Cam camera : cameraList) {
            map.addMarker( new MarkerOptions()
                    .position(new LatLng(camera.getLat(), camera.getLong()))
                    .title(camera.getDesc())
            );
        }

        Log.d("LOCATION", "camera markers shown");
    }
}