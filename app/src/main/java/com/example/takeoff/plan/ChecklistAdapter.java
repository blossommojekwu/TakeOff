package com.example.takeoff.plan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//CheckList Adapter: responsible for displaying data from the model into a row in the recycle view
public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {

    public interface OnClickListener{
        void onItemClicked(int position);
    }

    public interface OnLongClickListener {
        void onItemLongClicked(int position);
    }

    List<String> checklistItems;
    OnLongClickListener longClickListener;
    OnClickListener clickListener;

    public ChecklistAdapter(List<String> checklistItems, OnClickListener clickListener, OnLongClickListener longClickListener){
        this.checklistItems = checklistItems;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //use layout inflator to inflate a view
        View checkListView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        //wrap it inside a View Holder and return it
        return new ViewHolder(checkListView);
    }

    //takes data at position and put it in view holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //grab the item at the position
        String checklistItem = checklistItems.get(position);
        //bind the item into the specified view holder
        holder.bind(checklistItem);
    }

    @Override
    public int getItemCount() {
        return checklistItems.size();
    }

    //define view holder
    //container to provide easy access to views that represent each row of the list
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvChecklistItem;

        public ViewHolder(View checklistItemView) {
            super(checklistItemView);
            tvChecklistItem = checklistItemView.findViewById(android.R.id.text1);
        }
        //update the view inside the view holder with this data
        //want to update item like remove --> need position to edit text on click
        public void bind(String checklistItem) {
            tvChecklistItem.setText(checklistItem);
            tvChecklistItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClicked(getAdapterPosition());
                }
            });
            tvChecklistItem.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View view) {
                    //notify the listener about position that was long pressed
                    longClickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}