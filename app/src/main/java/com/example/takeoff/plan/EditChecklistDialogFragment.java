package com.example.takeoff.plan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.takeoff.R;
import com.google.android.material.button.MaterialButton;

public class EditChecklistDialogFragment extends DialogFragment {

    private EditText mEditChecklistText;
    private MaterialButton mBtnSave;

    public EditChecklistDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static EditChecklistDialogFragment newInstance(String title) {
        EditChecklistDialogFragment frag = new EditChecklistDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_checklist, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mEditChecklistText = (EditText) view.findViewById(R.id.etChecklistItem);
        mBtnSave = view.findViewById(R.id.btnSave);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Plan");
        getDialog().setTitle(title);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.95);
        // Show soft keyboard automatically and request focus to field
        mEditChecklistText.requestFocus();
        getDialog().getWindow().setLayout(width, height);
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newText = mEditChecklistText.getText().toString();
                EditChecklistDialogListener listener = (EditChecklistDialogListener) getTargetFragment();
                listener.onFinishEditDialog(newText);
                dismiss();
            }
        });
    }

    // Defines the listener interface
    public interface EditChecklistDialogListener {
        void onFinishEditDialog(String inputText);
    }
}