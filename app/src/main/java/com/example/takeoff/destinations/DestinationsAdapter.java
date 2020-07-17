package com.example.takeoff.destinations;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeoff.R;
import com.example.takeoff.models.Destination;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;

import java.util.List;

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
        //take viewholder passed in and pass data of the post into that viewholder
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
        }
    }
}
            /*
            setPhoto(place);
        }

        private void setPhoto(Place place){
            // Get the photo metadata.
            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.w(TAG, "No photo metadata.");
                return;
            }
            final PhotoMetadata photoMetadata = metadata.get(0);

            // Get the attribution text.
            final String attributions = photoMetadata.getAttributions();

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                //
                ivDestinationImage.setImageBitmap(bitmap);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                    // TODO: Handle error with given status code.
                }
            });
        }
        */
