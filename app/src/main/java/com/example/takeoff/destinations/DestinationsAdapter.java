package com.example.takeoff.destinations;

import android.content.Context;
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

import java.util.List;

/** DestinationsAdapter:
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

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDestinationName;
        ImageView ivDestinationImage;
        TextView tvDestionationDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDestinationName = itemView.findViewById(R.id.tvDestinationName);
            ivDestinationImage = itemView.findViewById(R.id.ivDestinationImage);
            tvDestionationDescription = itemView.findViewById(R.id.tvDestinationDescription);
        }

        public void bind(Destination destination) {
            //Bind the post data to the view elements
            tvDestinationName.setText(destination.getName());
            tvDestionationDescription.setText(destination.getDescription());
            ParseFile image = destination.getImage();
            if (image != null) {
                Glide.with(mContext).load(image.getUrl()).into(ivDestinationImage);
            }
        }
    }
}