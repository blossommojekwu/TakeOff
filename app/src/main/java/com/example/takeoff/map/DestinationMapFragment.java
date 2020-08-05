package com.example.takeoff.map;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import com.example.takeoff.R;
import com.example.takeoff.destinations.MainActivity;
import com.example.takeoff.models.Destination;
import com.example.takeoff.models.Hotel;
import com.example.takeoff.models.VisitPlace;
import com.example.takeoff.stay.HotelsAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.parceler.Parcels;
import java.util.List;

/**
 * DestinationMapFragment:
 * - places Markers at visit place locations
 * - places Marker at Destination searched
 */
public class DestinationMapFragment extends Fragment implements OnMapReadyCallback, HotelsAdapter.OnHotelClickListener {

    public static final String TAG = "DestinationMapFragment";
    private static final int DEFAULT_ZOOM = 13;
    private List<VisitPlace> mVisitPlaces;
    private Destination mDestination;
    private Hotel mHotelClicked;
    private LatLng mHotelClickedLocation;
    private LatLng mDestinationLocation;
    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private LatLng mFirstPlaceLocation;

    public DestinationMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mapView = inflater.inflate(R.layout.fragment_destination_map, container, false);
        if (this.getArguments() != null) {
            mVisitPlaces = this.getArguments().getParcelableArrayList(MainActivity.KEY_VISIT_PLACES);
            mDestination = (Destination) Parcels.unwrap(this.getArguments().getParcelable(MainActivity.KEY_DESTINATION));
            mHotelClicked = (Hotel) Parcels.unwrap(this.getArguments().getParcelable(MainActivity.KEY_HOTEL_CLICKED));
        }
        if (mDestination != null) {
            Log.i(TAG, "MAP DESTINATION: " + mDestination.getName());
            mDestinationLocation = new LatLng(mDestination.getLocation().getLatitude(), mDestination.getLocation().getLongitude());
        }
        if (mHotelClicked != null){
            mHotelClickedLocation = new LatLng(mHotelClicked.getLocation().getLatitude(), mHotelClicked.getLocation().getLongitude());
        }
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.destinationMap);
        mapFragment.getMapAsync(this);
        return mapView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if (mDestination != null) {
            mMap.addMarker(new MarkerOptions().position(mDestinationLocation)
                    .title(mDestination.getName())
                    .snippet(mDestination.getAddress())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            // Position the map's camera at the location of the destination marker.
            Log.i(TAG, "Map City: " + mDestination.getAddress());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDestinationLocation, DEFAULT_ZOOM));
        }
        if (mVisitPlaces != null && mVisitPlaces.size() > 0){
            mFirstPlaceLocation = new LatLng(mVisitPlaces.get(0).getLocation().getLatitude(), mVisitPlaces.get(0).getLocation().getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mFirstPlaceLocation, DEFAULT_ZOOM));
            for (VisitPlace visitPlace : mVisitPlaces){
                LatLng placeLocation = new LatLng(visitPlace.getLocation().getLatitude(), visitPlace.getLocation().getLongitude());
                Marker placeMarker = mMap.addMarker(new MarkerOptions().position(placeLocation)
                        .title(visitPlace.getName())
                        .snippet(visitPlace.getAddress())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                dropPinEffect(placeMarker);
            }
        }
        if (mHotelClicked != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mHotelClickedLocation, DEFAULT_ZOOM));
            Marker hotelMarker = mMap.addMarker(new MarkerOptions().position(mHotelClickedLocation)
                    .title(mHotelClicked.getName())
                    .snippet(mHotelClicked.getAddress())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            dropPinEffect(hotelMarker);
        }
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
    }

    private void dropPinEffect(final Marker marker) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator =
                new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                } else { // done elapsing, show window
                    marker.showInfoWindow();
                }
            }
        });
    }

    @Override
    public void onHotelClick(Hotel hotel) {
        mHotelClicked = hotel;
        if (mHotelClicked != null){
            Log.i(TAG, "Hotel Clicked: " + mHotelClicked.getName());
        }
    }
}