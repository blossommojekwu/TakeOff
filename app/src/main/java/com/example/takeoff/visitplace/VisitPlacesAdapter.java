package com.example.takeoff.visitplace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.takeoff.R;
import com.example.takeoff.models.VisitPlace;

import java.util.List;

public class VisitPlacesAdapter extends RecyclerView.Adapter<VisitPlacesAdapter.ViewHolder> {

    //context to inflate view and position
    private Context mContext;
    private List<VisitPlace> mVisitPlaces;

    public VisitPlacesAdapter(Context context, List<VisitPlace> visitPlaces){
        this.mContext = context;
        this.mVisitPlaces = visitPlaces;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_visitplace, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VisitPlace visitPlace = mVisitPlaces.get(position);
        holder.bind(visitPlace);
    }

    @Override
    public int getItemCount() {
        return mVisitPlaces.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mVisitPlaces.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvVisitPlaceName;
        private ImageView mIvVisitPlaceImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvVisitPlaceName = itemView.findViewById(R.id.tvVisitPlaceName);
            mIvVisitPlaceImage = itemView.findViewById(R.id.ivVisitPlaceImage);
        }

        public void bind(VisitPlace visitPlace) {
            //Bind the visit place data to the view elements
            mTvVisitPlaceName.setText(visitPlace.getName());
            if (visitPlace.getPhotoURL() != null){
                Glide.with(mContext).load(visitPlace.getPhotoURL()).transform(new CenterInside(),new RoundedCorners(R.dimen.visitPlaceRadius)).into(mIvVisitPlaceImage);
            }
        }
    }
}