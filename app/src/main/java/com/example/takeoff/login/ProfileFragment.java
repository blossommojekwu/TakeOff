package com.example.takeoff.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.takeoff.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

/**
 *
 */
public class ProfileFragment extends DialogFragment {

    public static final String TAG = "ProfileFragment";
    private ImageView mIvProfilePicture;
    private Button mBtnChangeProfile;
    private Button mBtnLogout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String title) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIvProfilePicture = view.findViewById(R.id.ivProfilePicture);
        mBtnChangeProfile = view.findViewById(R.id.btnChangeProfile);
        mBtnLogout = view.findViewById(R.id.btnLogout);

        ParseFile profilePic = ParseUser.getCurrentUser().getParseFile("profilePicture");
        if (profilePic != null){
            Glide.with(this).load(profilePic.getUrl()).fitCenter().transform(new CircleCrop()).into(mIvProfilePicture);
        }

        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}