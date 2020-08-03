package com.example.takeoff.destinations;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.takeoff.R;
import com.example.takeoff.models.Destination;
import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;
import java.util.Objects;

/**
 * DestinationsAdapter:
 * - displays data from Destination model to row(item_destination cardview) in Recycler View
 */
public class DestinationsAdapter extends RecyclerView.Adapter<DestinationsAdapter.ViewHolder> {

    public static final String TAG = "DestinationsAdapter";
    private Context mContext;
    private List<Destination> mDestinations;
    private Destination mRecentlyDeletedDestination;
    private int mRecentlyDeletedPosition;
    private DestinationsFragment mDestinationsFragment;

    public DestinationsAdapter(Context context, List<Destination> destinations, DestinationsFragment destinationsFragment) {
        this.mContext = context;
        this.mDestinations = destinations;
        this.mDestinationsFragment = destinationsFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_destination, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationsAdapter.ViewHolder holder, int position) {
        Destination destination = mDestinations.get(position);
        //take viewholder passed in and pass data of the destination into that viewholder
        holder.bind(destination);
    }

    // Clean all elements of the recycler
    public void clear() {
        mDestinations.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDestinations.size();
    }

    public void deleteItem(int position){
        if (mDestinations.size() > 0){
            mRecentlyDeletedDestination = mDestinations.get(position);
            mRecentlyDeletedPosition = position;
            deleteHotel(mDestinations.get(position));
            mDestinations.remove(position);
            notifyItemRemoved(position);
            showUndoSnackbar();
        }
    }

    private void showUndoSnackbar() {
        View view = mDestinationsFragment.getView();
        Snackbar snackbar = Snackbar.make(view, R.string.snackbar_text,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snack_bar_undo, v -> undoDelete());
        snackbar.show();
    }

    public void undoDelete() {
        mDestinations.add(mRecentlyDeletedPosition,
                mRecentlyDeletedDestination);
        notifyItemInserted(mRecentlyDeletedPosition);
        Destination deletedDestination = new Destination();
        deletedDestination.setUser(mRecentlyDeletedDestination.getUser());
        deletedDestination.setName(mRecentlyDeletedDestination.getName());
        deletedDestination.setAddress(mRecentlyDeletedDestination.getAddress());
        deletedDestination.setDescription(mRecentlyDeletedDestination.getDescription());
        deletedDestination.setTypes(mRecentlyDeletedDestination.getTypes());
        deletedDestination.setLocation(mRecentlyDeletedDestination.getLocation());
        deletedDestination.setWebsite(mRecentlyDeletedDestination.getWebsite());
        deletedDestination.setImage(mRecentlyDeletedDestination.getImage());
        deletedDestination.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(mContext, R.string.saving_error, Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Destination undo delete was successful!");
            }
        });
    }

    public Context getContext() {
        return mContext;
    }

    private void deleteHotel(Destination destination){
        destination.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while deleting", e);
                    Toast.makeText(mContext, R.string.deleting_error, Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Destination delete was successful!");
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTvDestinationName;
        private ImageView mIvDestinationImage;
        private TextView mTvDestinationAddress;
        //limited destination types to top 2 for better screen fit and importance
        private TextView mTvDestinationType1;
        private TextView mTvDestinationType2;
        private TextView mTvDestinationWebsite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvDestinationName = itemView.findViewById(R.id.tvDestinationName);
            mIvDestinationImage = itemView.findViewById(R.id.ivDestinationImage);
            mTvDestinationAddress = itemView.findViewById(R.id.tvDestinationAddress);
            mTvDestinationType1 = itemView.findViewById(R.id.tvDestinationType1);
            mTvDestinationType2 = itemView.findViewById(R.id.tvDestinationType2);
            mTvDestinationWebsite = itemView.findViewById(R.id.tvDestinationWebsite);
            itemView.setOnClickListener(this);
        }

        //when the user clicks on a row, show DestinationDetailsActivity for the selected destination
        @Override
        public void onClick(View view) {
            //get destination position
            int position = getAdapterPosition();
            //position must be valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION){
                //get destination at position
                Destination destination = mDestinations.get(position);
                //create intent for new activity
                Intent intent = new Intent(mContext, DestinationMapActivity.class);
                //serialize destination using parceler w/ short name as key
                intent.putExtra(Destination.class.getSimpleName(), Parcels.wrap(destination));
                //show the activity
                mContext.startActivity(intent);
            }
        }

        public void bind(Destination destination) {
            //Bind the post data to the view elements
            mTvDestinationName.setText(destination.getName());
            mTvDestinationAddress.setText(destination.getDescription());
            mTvDestinationType1.setVisibility(View.GONE);
            mTvDestinationType2.setVisibility(View.GONE);
            ParseFile image = destination.getImage();
            if (image != null) {
                Glide.with(mContext).load(image.getUrl()).into(mIvDestinationImage);
            }
            if (destination.getTypes() != null){
                mTvDestinationType1.setVisibility(View.VISIBLE);
                mTvDestinationType1.setText(destination.getTypes().get(0));
                if (destination.getTypes().size() > 1) {
                    mTvDestinationType2.setVisibility(View.VISIBLE);
                    mTvDestinationType2.setText(destination.getTypes().get(1));
                }
            }
            if (destination.getWebsite() != null){
                mTvDestinationWebsite.setText(destination.getWebsite());
            }
        }
    }
}