package com.example.takeoff.destinations;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.takeoff.R;
import com.example.takeoff.databinding.ActivityMapBinding;
import com.example.takeoff.models.Destination;
import com.example.takeoff.models.Hotel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.parceler.Parcels;

/**
 * DestinationMapActivity contains:
 * - Marker set at location of destination visible when user clicks on destination card view
 * - Marker set at the user's device current location accessible through My Location button on top right corner
 * - Marker placed at hotel clicked with current destination
 * - Zoom controls on bottom right corner
 * - Map toolbar on bottom right that appears when user taps a marker and provides quick access to Google Maps app
 */
public class DestinationMapActivity extends AppCompatActivity implements OnMapReadyCallback{

    public static final String TAG = "DestinationMapActivity";
    private static final int DEFAULT_ZOOM = 14;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Destination mDestination;
    private Hotel mHotel;
    private LatLng mLocation;
    private LatLng mHotelLocation;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mCurrentLocation;
    private UiSettings mUiSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "Destination MapView", Toast.LENGTH_SHORT).show();
        ActivityMapBinding mapBinding = ActivityMapBinding.inflate(getLayoutInflater());
        View mapView = mapBinding.getRoot();
        setContentView(mapView);

        mDestination = (Destination) Parcels.unwrap(getIntent().getParcelableExtra(Destination.class.getSimpleName()));
        mHotel = (Hotel) Parcels.unwrap(getIntent().getParcelableExtra(Hotel.class.getSimpleName()));
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (mDestination != null){
            Log.i(TAG, "MAP DESTINATION: " + mDestination.getName());
            mLocation = new LatLng(mDestination.getLocation().getLatitude(), mDestination.getLocation().getLongitude());
        }
        if (mHotel != null){
            Log.i(TAG, "MAP HOTEL: " + mHotel.getName());
            mHotelLocation = new LatLng(mHotel.getLocation().getLatitude(), mHotel.getLocation().getLongitude());
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync( this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.mMap = map;
        if (mDestination != null) {
            mMap.addMarker(new MarkerOptions().position(mLocation)
                    .title(mDestination.getName())
                    .snippet(mDestination.getAddress()));
        }
        if (mHotel != null) {
            double lat = mHotel.getDestination().getParseGeoPoint("location").getLatitude();
            double lng = mHotel.getDestination().getParseGeoPoint("location").getLongitude();
            mLocation = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(mLocation)
                    .title(mHotel.getDestination().getString("name"))
                    .snippet(mHotel.getDestination().getString("address")));
            mMap.addMarker(new MarkerOptions().position(mHotelLocation)
                    .title(mHotel.getName())
                    .snippet(mHotel.getAddress())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }
        // Position the map's camera at the location of the destination marker.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, DEFAULT_ZOOM));
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        // Prompt the user for permission.
        getLocationPermission();
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Get the current location of the device and set the position on the map.
        getDeviceLocation();
    }

     // Prompts the user for permission to use the device location.
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    // Handles the result of the request for location permissions.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    // Updates the map's UI settings based on whether the user has granted location permission.
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // Get the best and most recent location of the device, which may be null in rare cases when a location is not available.
    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mCurrentLocation = task.getResult();
                            Log.i(TAG, "Current Location: " + mCurrentLocation);
                            if (mCurrentLocation != null) {
                                LatLng mCurrentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(mCurrentLatLng)
                                .title("Me").snippet("My Location"));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
}