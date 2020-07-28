package com.example.takeoff.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("VisitPlace")
public class VisitPlace extends ParseObject {

    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_PHOTO_URL = "photoURL";
    public static final String KEY_DESTINATION = "destination";

    public String getName(){
        return getString(KEY_NAME);
    }

    public void setName(String name){
        put(KEY_NAME, name);
    }

    public String getAddress(){
        return getString(KEY_ADDRESS);
    }

    public void setAddress(String address){
        put(KEY_ADDRESS, address);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    public void setLocation(ParseGeoPoint parseGeoPoint) {
        put(KEY_LOCATION, parseGeoPoint);
    }

    public String getPhotoURL() {
        return getString(KEY_PHOTO_URL);
    }

    public void setPhotoURL(String photoURL){
        put(KEY_PHOTO_URL, photoURL);
    }

    public ParseObject getDestination(){
        return getParseObject(KEY_DESTINATION);
    }

    public void setDestination(ParseObject destination){
        put(KEY_DESTINATION, destination);
    }
}
