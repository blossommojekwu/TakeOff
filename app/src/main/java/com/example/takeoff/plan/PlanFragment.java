package com.example.takeoff.plan;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.takeoff.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlanFragment extends Fragment {

    public static final String TAG = "PlanFragment";
    private MaterialButton mBtnDatePicker;
    private TextView mTvSelectedDates;

    //initialize checklist items
    List<String> checklistItems;
    private FloatingActionButton mFloatingActionBtn;
    private RecyclerView mRvChecklist;
    private TextInputEditText mEtChecklistItem;
    ChecklistAdapter checklistAdapter;

    public PlanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnDatePicker = view.findViewById(R.id.btnDatePicker);
        mTvSelectedDates = view.findViewById(R.id.tvSelectedDates);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();

        //MaterialDatePicker
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText(R.string.select_dates);

        final MaterialDatePicker materialDatePicker = builder.build();

        mBtnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getFragmentManager(), "DATE_PICKER");
            }
        });
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                mTvSelectedDates.setText("Selected Dates: " + materialDatePicker.getHeaderText());
            }
        });

        //CHECKLIST
        mFloatingActionBtn = view.findViewById(R.id.floatingActionBtn);
        mRvChecklist = view.findViewById(R.id.rvChecklist);
        mEtChecklistItem = view.findViewById(R.id.etChecklistItem);

       loadItems();

       ChecklistAdapter.OnLongClickListener onLongClickListener = new ChecklistAdapter.OnLongClickListener() {
           @Override
           public void onItemLongClicked(int position) {
               //delete the item from the model
               checklistItems.remove(position);
               //notify the adapter
               checklistAdapter.notifyItemRemoved(position);
               Toast.makeText(getContext(), "Won't be ...", Toast.LENGTH_SHORT).show();
               saveItems();
           }
       };

       ChecklistAdapter.OnClickListener onClickListener = new ChecklistAdapter.OnClickListener(){
           @Override
           public void onItemClicked(int position) {
               Log.d(TAG, "Single click at position " + position);
           }
       };
       checklistAdapter = new ChecklistAdapter(checklistItems, onClickListener, onLongClickListener);
       mRvChecklist.setAdapter(checklistAdapter);
       mRvChecklist.setLayoutManager(new LinearLayoutManager(getContext()));

       mFloatingActionBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String checklistItem = mEtChecklistItem.getText().toString();
               //add item to the model
               checklistItems.add(checklistItem);
               //notify adapter that an item is inserted
               checklistAdapter.notifyItemInserted(checklistItems.size()-1);
               //clear edit text once submitted
               mEtChecklistItem.setText("");
           }
       });
    }
    //return file where we store list of checklist items
    private File getDataFile(){
        return new File(getContext().getFilesDir(), "checklistData.txt");
    }
    //this function will load items by reading every line of the data file
    private void loadItems(){
        try {
            checklistItems = new ArrayList<>(org.apache.commons.io.FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e(TAG, "Error reading items", e);
            checklistItems = new ArrayList<>();
        }
    }
    //this function saves items by writing them into the data file
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(), checklistItems);
        } catch (IOException e) {
            Log.e(TAG, "Error writing items", e);
        }
    }
}