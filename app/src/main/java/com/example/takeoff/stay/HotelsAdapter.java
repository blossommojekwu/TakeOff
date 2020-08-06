package com.example.takeoff.stay;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.takeoff.R;
import com.example.takeoff.models.Hotel;

import java.util.List;

/**
 * HotelAdapter:
 * - displays data from Hotel model to viewholder in Recycler View
 */
public class HotelsAdapter extends RecyclerView.Adapter<HotelsAdapter.ViewHolder> {

    public interface OnHotelClickListener{
        void onHotelClick(Hotel hotel);
    }

    //context to inflate view and position
    private Context mContext;
    private List<Hotel> mHotels;
    private OnHotelClickListener mHotelClickListener;

    public HotelsAdapter(Context context, List<Hotel> hotels, OnHotelClickListener hotelClickListener){
        this.mContext = context;
        this.mHotels = hotels;
        this.mHotelClickListener = hotelClickListener;
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
        private Button mBtnFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvHotelName = itemView.findViewById(R.id.tvHotelName);
            mIvHotelImage = itemView.findViewById(R.id.ivHotelImage);
            mTvHotelAddress = itemView.findViewById(R.id.tvHotelAddress);
            mTvHotelWebsite = itemView.findViewById(R.id.tvHotelWebsite);
            mTvHotelPhoneNumber = itemView.findViewById(R.id.tvHotelPhoneNumber);
            mRatingBarHotel = itemView.findViewById(R.id.ratingBarHotel);
            mBtnFavorite = itemView.findViewById(R.id.btnFavorite);
            itemView.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        mBtnFavorite.setVisibility(View.VISIBLE);
                        //get hotel position
                        int position = getAdapterPosition();
                        //position must be valid, i.e. actually exists in the view
                        if (position != RecyclerView.NO_POSITION) {
                            //get hotel at position
                            Hotel hotel = mHotels.get(position);
                            mHotelClickListener.onHotelClick(hotel);
                        }
                        return super.onDoubleTap(e);
                    }
                });
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    gestureDetector.onTouchEvent(motionEvent);
                    return true;
                }
            });
        }

        public void bind(Hotel hotel) {
            //Bind the hotel data to the view elements
            mTvHotelName.setText(hotel.getName());
            mTvHotelAddress.setText(hotel.getAddress());
            mTvHotelWebsite.setText(hotel.getWebsite());
            mTvHotelPhoneNumber.setText(hotel.getPhoneNumber());
            mRatingBarHotel.setRating((float) hotel.getRating());
            if (hotel.getImageURL() != null){
                Glide.with(mContext).load(hotel.getImageURL()).fitCenter().into(mIvHotelImage);
            }
        }

        @Override
        public void onClick(View view) {
            //get hotel position
            int position = getAdapterPosition();
            //position must be valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                //get hotel at position
                Hotel hotel = mHotels.get(position);
                mHotelClickListener.onHotelClick(hotel);
            }
        }
    }
}