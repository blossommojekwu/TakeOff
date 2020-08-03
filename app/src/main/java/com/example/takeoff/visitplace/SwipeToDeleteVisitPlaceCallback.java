package com.example.takeoff.visitplace;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeoff.R;

public class SwipeToDeleteVisitPlaceCallback extends ItemTouchHelper.SimpleCallback{

    private VisitPlacesAdapter mVisitPlacesAdapter;
    private Drawable mIcon;
    private ColorDrawable mBackground;
    private int mColor;

    public SwipeToDeleteVisitPlaceCallback(VisitPlacesAdapter visitPlacesAdapter) {
        super(0, ItemTouchHelper.RIGHT);
        mVisitPlacesAdapter = visitPlacesAdapter;
        mIcon = ContextCompat.getDrawable(mVisitPlacesAdapter.getContext(), R.drawable.ic_cancel_clear);
        mColor = Color.rgb(255, 0, 0);
        mBackground = new ColorDrawable(mColor);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        mVisitPlacesAdapter.deleteItem(position);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;
        int iconMargin = (itemView.getHeight() - mIcon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (mIcon.getIntrinsicHeight() / 2);
        int iconBottom = itemView.getBottom() - (mIcon.getIntrinsicHeight() / 2);

        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + backgroundCornerOffset;
            int iconRight = itemView.getLeft() + (backgroundCornerOffset * 4) + iconMargin;

            mBackground.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX / 4) + backgroundCornerOffset,
                    itemView.getBottom());
            mIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        } else { // view is unSwiped
            mBackground.setBounds(0, 0, 0, 0);
        }
        mBackground.draw(c);
        mIcon.draw(c);
    }
}