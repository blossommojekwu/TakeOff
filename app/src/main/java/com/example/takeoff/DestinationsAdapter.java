package com.example.takeoff;

import android.content.Context;

import com.example.takeoff.models.Destination;

import java.util.List;

public class DestinationsAdapter{

    private Context mContext;
    private List<Destination> mDestinations;

    public DestinationsAdapter(Context context, List<Destination> destinations){
        this.mContext = context;
        this.mDestinations = destinations;
    }

}