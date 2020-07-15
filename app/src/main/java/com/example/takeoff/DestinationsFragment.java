package com.example.takeoff;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 * * create an instance of this fragment.
 */

public class DestinationsFragment extends Fragment {

    public static final String TAG = "DestinationsFragment";
    private SwipeRefreshLayout mSwipeContainer;
    private RecyclerView mRvDestinations;

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
        // 1. create the adapter
        // 2. create the data source
        // 3. set the adapter on the recycler view
        // 4. set the layout manager on the recycler view
    }
}