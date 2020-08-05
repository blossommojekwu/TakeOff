package com.example.takeoff.stay;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.takeoff.R;
import com.example.takeoff.models.Hotel;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * StayFragment:
 * - display feed of hotels in destination
 */
public class StayFragment extends Fragment {

    public static final String TAG = "StayFragment";
    public static final int QUERY_LIMIT = 20;
    public static final int NUM_COLUMNS = 2;
    private SwipeRefreshLayout mHotelSwipeContainer;
    private Hotel mHotelClicked;
    private RecyclerView mRvHotels;
    private OnGetHotelClickedListener mGetHotelClickedListener;
    protected HotelsAdapter mAdapter;
    protected List<Hotel> mAllHotels;

    public StayFragment() {
        // Required empty public constructor
    }

    public interface OnGetHotelClickedListener {
        void getHotel(Hotel hotel);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            mGetHotelClickedListener = (OnGetHotelClickedListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stay, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HotelsAdapter.OnHotelClickListener  onHotelClickListener = new HotelsAdapter.OnHotelClickListener() {
            @Override
            public void onHotelClick(Hotel hotel) {
                mHotelClicked = hotel;
                mGetHotelClickedListener.getHotel(hotel);
                Toast.makeText(getContext(), "See: " + mHotelClicked.getName(), Toast.LENGTH_SHORT).show();
            }
        };
        mRvHotels = view.findViewById(R.id.rvHotels);
        //steps to use the recycler view
        // 0. Create layout for one row in the list
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        // 1. create the adapter
        // 2. create the data source
        mAllHotels = new ArrayList<>();
        mAdapter = new HotelsAdapter(getContext(), mAllHotels, onHotelClickListener);
        mHotelSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.hotelSwipeContainer);
        // Setup refresh listener which triggers new data loading
        mHotelSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchFeed(0);
            }
        });
        mHotelSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        // 3. set the adapter on the recycler view
        mRvHotels.setAdapter(mAdapter);
        // 4. set the layout manager on the recycler view
        mRvHotels.setLayoutManager(new GridLayoutManager(getContext(), NUM_COLUMNS));//(linearLayoutManager);
        queryHotels();
    }

    private void fetchFeed(int i) {
        mAdapter.clear();
        queryHotels();
        mHotelSwipeContainer.setRefreshing(false);
    }

    //calculates the distance in statute miles between 2 locations using GeoDataSource (TM) products
    private double latLngDistance(LatLng location1, LatLng location2) {
        double lat1 = location1.latitude;
        double lat2 = location2.latitude;
        double lon1 = location1.longitude;
        double lon2 = location2.longitude;
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            return dist;
        }
    }

    private LatLng convertGeoPoint(ParseGeoPoint geoPoint){
        LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
        return latLng;
    }

    private void queryHotels() {
        //Specify which class to query
        ParseQuery<Hotel> query = ParseQuery.getQuery(Hotel.class);
        // only show hotels with pointer to current destination
        query.include(Hotel.KEY_DESTINATION);
        query.setLimit(QUERY_LIMIT);
        query.addDescendingOrder(Hotel.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Hotel>() {
            @Override
            public void done(List<Hotel> hotels, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with getting hotels for destination", e);
                    return;
                }
                for (Hotel hotel : hotels){
                    Log.i(TAG, "Hotel: " + hotel.getName() + ", address: " + hotel.getAddress());
                }
                mAllHotels.addAll(hotels);
                Collections.sort(mAllHotels, new Comparator<Hotel>() {
                    @Override
                    public int compare(Hotel hotel1, Hotel hotel2) {
                        LatLng hotelLocation1 = convertGeoPoint(hotel1.getLocation());
                        LatLng hotelLocation2 = convertGeoPoint(hotel2.getLocation());
                        LatLng destinationLocation = new LatLng(0,0);
                        if (hotel1.getDestination() != null){
                            destinationLocation = convertGeoPoint(hotel1.getDestination().getParseGeoPoint("location"));
                        }
                        //LatLng destinationLocation = convertGeoPoint(hotel1.getDestination().getParseGeoPoint("location"));
                        return (int) (latLngDistance(hotelLocation1, destinationLocation) - latLngDistance(hotelLocation2, destinationLocation));
                    }
                });
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}