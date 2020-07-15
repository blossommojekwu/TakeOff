package com.example.takeoff;

import android.app.Application;

import com.example.takeoff.models.Destination;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Register parse models
        ParseObject.registerSubclass(Destination.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("blossom-takeoff") // should correspond to APP_ID env variable
                .clientKey(String.valueOf(R.string.master_key))  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://blossom-takeoff.herokuapp.com/parse").build());
    }
}