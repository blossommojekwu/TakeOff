package com.example.takeoff.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.takeoff.destinations.MainActivity;
import com.example.takeoff.R;
import com.example.takeoff.databinding.ActivityLoginBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/** Login Activity:
 * - allows user to enter valid username and password and login to view DestinationFragment
 */

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private TextInputEditText mEtUsername;
    private TextInputEditText mEtPassword;
    private Button mBtnLogin;
    private Button mBtnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        View loginView = loginBinding.getRoot();
        setContentView(loginView);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.ic_flight_takeoff_24);
        actionBar.setDisplayUseLogoEnabled(true);

        //if user already logged in, go to MainActivity
        if (ParseUser.getCurrentUser() != null){
            getMainActivity();
        }

        mEtUsername = loginBinding.etUsername;
        mEtPassword = loginBinding.etPassword;
        mBtnLogin = loginBinding.btnLogin;
        mBtnSignUp = loginBinding.btnSignUp;
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Clicked Login Button");
                String username = mEtUsername.getText().toString();
                String password = mEtPassword.getText().toString();
                loginUser(username, password);
            }
        });

        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);
        //navigate to main activity if user has signed in properly
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with Login", e);
                    Toast.makeText(LoginActivity.this, R.string.incorrect_login, Toast.LENGTH_SHORT).show();
                    return;
                }
                getMainActivity();
                Toast.makeText(LoginActivity.this, R.string.welcome, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //Finish LoginActivity once navigated to MainActivity
        finish();
    }
}