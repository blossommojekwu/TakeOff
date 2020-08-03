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
import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.List;

public class VisitPlacesAdapter extends RecyclerView.Adapter<VisitPlacesAdapter.ViewHolder> {

    //context to inflate view and position
    public static final String TAG = "VisitPlacesAdapter";
    private Context mContext;
    private List<VisitPlace> mVisitPlaces;
    private VisitPlace mRecentlyDeletedPlace;
    private int mRecentlyDeletedPosition;
    private VisitPlaceFragment mVisitPlaceFragment;

    public VisitPlacesAdapter(Context context, List<VisitPlace> visitPlaces, VisitPlaceFragment visitPlaceFragment){
        this.mContext = context;
        this.mVisitPlaces = visitPlaces;
        this.mVisitPlaceFragment = visitPlaceFragment;
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
        showUndoSnackbar();
    }

    private void showUndoSnackbar() {
        View view = mVisitPlaceFragment.getView();
        Snackbar snackbar = Snackbar.make(view, R.string.snackbar_text,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snack_bar_undo, v -> undoDelete());
        snackbar.show();
    }

    private void undoDelete() {
        mVisitPlaces.add(mRecentlyDeletedPosition, mRecentlyDeletedPlace);
        notifyItemInserted(mRecentlyDeletedPosition);
        VisitPlace deletedPlace = newVisitPlace();
        deletedPlace.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(mContext, R.string.saving_error, Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Place undo delete was successful!");
            }
        });
    }

    private VisitPlace newVisitPlace(){
        VisitPlace deletedPlace = new VisitPlace();
        deletedPlace.setName(mRecentlyDeletedPlace.getName());
        deletedPlace.setAddress(mRecentlyDeletedPlace.getAddress());
        deletedPlace.setLocation(mRecentlyDeletedPlace.getLocation());
        deletedPlace.setPhotoURL(mRecentlyDeletedPlace.getPhotoURL());
        deletedPlace.setDestination(mRecentlyDeletedPlace.getDestination());
        return deletedPlace;
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