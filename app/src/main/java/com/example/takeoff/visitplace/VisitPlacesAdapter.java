package com.example.takeoff.visitplace;

import android.content.Context;
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
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.takeoff.R;
import com.example.takeoff.models.VisitPlace;
import com.parse.DeleteCallback;
import com.parse.ParseException;

import java.util.List;

public class VisitPlacesAdapter extends RecyclerView.Adapter<VisitPlacesAdapter.ViewHolder> {

    //context to inflate view and position
    public static final String TAG = "VisitPlacesAdapter";
    private Context mContext;
    private List<VisitPlace> mVisitPlaces;
    private VisitPlace mRecentlyDeletedPlace;
    private int mRecentlyDeletedPosition;

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

    public Context getContext() {
        return mContext;
    }

    public void deleteItem(int position) {
        mRecentlyDeletedPlace = mVisitPlaces.get(position);
        mRecentlyDeletedPosition = position;
        deletePlace(mVisitPlaces.get(position));
        mVisitPlaces.remove(position);
        notifyItemRemoved(position);
    }

    private void deletePlace(VisitPlace visitPlace){
        visitPlace.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while deleting", e);
                    Toast.makeText(mContext, R.string.deleting_error, Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Place delete was successful!");
            }
        });
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