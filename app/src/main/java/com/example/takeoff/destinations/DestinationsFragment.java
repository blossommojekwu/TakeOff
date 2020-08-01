package com.example.takeoff.destinations;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.takeoff.R;
import com.example.takeoff.models.Destination;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * DestinationsFragment contains:
 * - creation of layout for each row in recyclerview
 * - sets adapter and layout manager on recyclerview to populate feed
 * - pull to refresh feed feature
 */
public class DestinationsFragment extends Fragment {

    public static final String TAG = "DestinationsFragment";
    public static final int QUERY_LIMIT = 5;
    private SwipeRefreshLayout mSwipeContainer;
    private RecyclerView mRvDestinations;
    private ItemTouchHelper mItemTouchHelper;
    protected DestinationsAdapter mAdapter;
    protected List<Destination> mAllDestinations;

    public DestinationsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_destinations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvDestinations = view.findViewById(R.id.rvDestinations);
        //steps to use the recycler view
        // 0. Create layout for one row in the list
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        // 1. create the adapter
        // 2. create the data source
        mAllDestinations = new ArrayList<>();
        mAdapter = new DestinationsAdapter(getContext(), mAllDestinations);
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchFeed(0);
            }
        });
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        // 3. set the adapter on the recycler view
        mRvDestinations.setAdapter(mAdapter);
        // 4. set the layout manager on the recycler view
        mRvDestinations.setLayoutManager(linearLayoutManager);
        mItemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(mAdapter));
        mItemTouchHelper.attachToRecyclerView(mRvDestinations);
        queryDestinations();
    }

    private void fetchFeed(int i) {
        // CLEAR OUT old items before appending in the new ones
        mAdapter.clear();
        // ...the data has come back, add new items to your adapter...
        queryDestinations();
        // Now we call setRefreshing(false) to signal refresh has finished
        mSwipeContainer.setRefreshing(false);
    }

    protected void queryDestinations(){
        //Specify which class to query
        ParseQuery<Destination> query = ParseQuery.getQuery(Destination.class);
        query.include(Destination.KEY_USER);
        query.setLimit(QUERY_LIMIT);
        query.addDescendingOrder(Destination.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Destination>() {
            @Override
            public void done(List<Destination> destinations, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with getting destinations", e);
                    return;
                }
                for (Destination destination : destinations){
                    Log.i(TAG, "Destination: " + destination.getName() + ", address: " + destination.getDescription());
                }
                //update data source and notify adapter that we got new data
                mAllDestinations.addAll(destinations);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}