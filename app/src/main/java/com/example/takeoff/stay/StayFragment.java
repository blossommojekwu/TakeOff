package com.example.takeoff.stay;

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

import com.example.takeoff.R;
import com.example.takeoff.models.Hotel;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
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
    private RecyclerView mRvHotels;
    protected HotelsAdapter mAdapter;
    protected List<Hotel> mAllHotels;

    public StayFragment() {
        // Required empty public constructor
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
        mRvHotels = view.findViewById(R.id.rvHotels);
        //steps to use the recycler view
        // 0. Create layout for one row in the list
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        // 1. create the adapter
        // 2. create the data source
        mAllHotels = new ArrayList<>();
        mAdapter = new HotelsAdapter(getContext(), mAllHotels);
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

    private void queryHotels() {
        //Specify which class to query
        ParseQuery<Hotel> query = ParseQuery.getQuery(Hotel.class);
        // only show hotels around current destination
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
                //update data source and notify adapter that we got new data
                mAllHotels.addAll(hotels);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}