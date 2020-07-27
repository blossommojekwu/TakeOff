package com.example.takeoff.stay;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.takeoff.R;
import com.example.takeoff.destinations.DestinationMapActivity;
import com.example.takeoff.models.Hotel;

import org.parceler.Parcels;

import java.util.List;

/**
 * HotelAdapter:
 * - displays data from Hotel model to viewholder in Recycler View
 */
public class HotelsAdapter extends RecyclerView.Adapter<HotelsAdapter.ViewHolder> {

    //context to inflate view and position
    private Context mContext;
    private List<Hotel> mHotels;

    public HotelsAdapter(Context context, List<Hotel> hotels){
        this.mContext = context;
        this.mHotels = hotels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_hotel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hotel hotel = mHotels.get(position);
        //bind the hotel data into the viewHolder
        holder.bind(hotel);
    }

    @Override
    public int getItemCount() {
        return mHotels.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mHotels.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTvHotelName;
        private ImageView mIvHotelImage;
        private TextView mTvHotelAddress;
        private TextView mTvHotelWebsite;
        private TextView mTvHotelPhoneNumber;
        private RatingBar mRatingBarHotel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvHotelName = itemView.findViewById(R.id.tvHotelName);
            mIvHotelImage = itemView.findViewById(R.id.ivHotelImage);
            mTvHotelAddress = itemView.findViewById(R.id.tvHotelAddress);
            mTvHotelWebsite = itemView.findViewById(R.id.tvHotelWebsite);
            mTvHotelPhoneNumber = itemView.findViewById(R.id.tvHotelPhoneNumber);
            mRatingBarHotel = itemView.findViewById(R.id.ratingBarHotel);
            itemView.setOnClickListener(this);
        }

        public void bind(Hotel hotel) {
            //Bind the hotel data to the view elements
            mTvHotelName.setText(hotel.getName());
            mTvHotelAddress.setText(hotel.getAddress());
            mTvHotelWebsite.setText(hotel.getWebsite());
            mTvHotelPhoneNumber.setText(hotel.getPhoneNumber());
            mRatingBarHotel.setRating((float) hotel.getRating());
            if (hotel.getImageURL() != null){
                Glide.with(mContext).load(hotel.getImageURL()).into(mIvHotelImage);
            }
        }

        @Override
        public void onClick(View view) {
            //get destination position
            int position = getAdapterPosition();
            //position must be valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION){
                //get destination at position
                Hotel hotel = mHotels.get(position);
                //create intent for new activity
                Intent intent = new Intent(mContext, DestinationMapActivity.class);
                //serialize destination using parceler w/ short name as key
                intent.putExtra(Hotel.class.getSimpleName(), Parcels.wrap(hotel));
                //show the activity
                mContext.startActivity(intent);
            }
        }
    }
}