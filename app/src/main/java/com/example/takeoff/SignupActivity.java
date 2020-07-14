package com.example.takeoff;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.takeoff.databinding.ActivitySignupBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;

import static com.example.takeoff.R.*;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SignupActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private TextInputEditText mEtSignupUsername;
    private TextInputEditText mEtSignupPassword;
    private MaterialButton mBtnSignUpPic;
    private ImageView mIvProfilePic;
    private MaterialButton mBtnCreateAccount;
    private File mProfilePicFile;
    private String mProfilePicFileName = "profile_photo.jpg";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignupBinding signupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        View signupView = signupBinding.getRoot();
        setContentView(signupView);

        mEtSignupUsername = signupBinding.etSignupUsername;
        mEtSignupPassword = signupBinding.etSignupPassword;
        mBtnSignUpPic = signupBinding.btnSignupPic;
        mIvProfilePic = signupBinding.ivProfilePic;
        mBtnCreateAccount = signupBinding.btnCreateAccount;

        mBtnSignUpPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        mBtnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = mEtSignupUsername.getText().toString();
                final String password = mEtSignupPassword.getText().toString();
                if (username.isEmpty() || password.isEmpty()){
                    Toast.makeText(SignupActivity.this,  R.string.create_account_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mProfilePicFile == null || mIvProfilePic.getDrawable() == null){
                    Toast.makeText(SignupActivity.this, R.string.need_profilepic, Toast.LENGTH_SHORT).show();
                    return;
                }
                final ParseFile photo = new ParseFile(mProfilePicFile);
                photo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        //if successful adds photo file to Parse User and signUpInbackground
                        if (e == null){
                            saveUser(username, password, photo);
                            ParseUser.logInInBackground(username, password, new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException e) {
                                    if (user != null){
                                        Log.i(TAG, string.login_new_user + ": " + username);
                                        Toast.makeText(SignupActivity.this, string.login_new_user, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void saveUser(String username, String password, ParseFile profilePicFile) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.put("profilePicture", profilePicFile);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while signing up", e);
                    Toast.makeText(SignupActivity.this, R.string.signup_error, Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "User save was successful!");
                Toast.makeText(SignupActivity.this, string.welcome, Toast.LENGTH_SHORT).show();
                //clear out previous data
                mEtSignupUsername.setText("");
                mEtSignupPassword.setText("");
                mIvProfilePic.setImageResource(0);
            }
        });
        Log.i(TAG, "New user: " + ParseUser.getCurrentUser());
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mProfilePicFile = getPhotoFileUri(mProfilePicFileName);

        Uri fileProvider = FileProvider.getUriForFile(this, "com.takeoff.fileprovider", mProfilePicFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(this.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(mProfilePicFile.getAbsolutePath());
                // Load the taken image into a preview
                mIvProfilePic.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}