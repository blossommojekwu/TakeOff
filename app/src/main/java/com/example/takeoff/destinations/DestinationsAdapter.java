package com.example.takeoff.destinations;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.takeoff.R;
import com.example.takeoff.models.Destination;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

/**
 * DestinationsAdapter:
 * - displays data from Destination model to row(item_destination cardview) in Recycler View
 */
public class DestinationsAdapter extends RecyclerView.Adapter<DestinationsAdapter.ViewHolder> {

    public static final String TAG = "DestinationsAdapter";
    private Context mContext;
    private List<Destination> mDestinations;

    public DestinationsAdapter(Context context, List<Destination> destinations) {
        this.mContext = context;
        this.mDestinations = destinations;
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTvDestinationName;
        private ImageView mIvDestinationImage;
        private TextView mTvDestinationAddress;
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