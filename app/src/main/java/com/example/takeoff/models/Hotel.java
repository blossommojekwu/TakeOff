package com.example.takeoff.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Hotel:
 * - class for hotel data model
 */
@Parcel(Parcel.Serialization.BEAN)
@ParseClassName("Hotel")
public class Hotel extends ParseObject{

    public static final String KEY_PLACE_ID = "placeId";
    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_WEBSITE = "website";
    public static final String KEY_IMAGE_URL = "imageURL";
    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_RATING = "rating";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_DESTINATION = "destination";

    // empty constructor needed by the Parceler library
    public Hotel() {
    }

    public static String hotelPlaceId(JSONObject jsonObject) throws JSONException{
        String placeId = jsonObject.getString("place_id");
        return placeId;
    }

    public static List<String> placeIdFromJSONArray(JSONArray searchJsonArray) throws JSONException {
        List<String> placeIds = new ArrayList<>();
        //iterate through json array and construct a movie for each element in array
        for (int i = 0; i < searchJsonArray.length(); i++) {
            placeIds.add(hotelPlaceId(searchJsonArray.getJSONObject(i)));
        }
        return placeIds;
    }

    public ParseObject getDestination(){
        return getParseObject(KEY_DESTINATION);
    }

    public void setDestination(ParseObject destination){
        put(KEY_DESTINATION, destination);
    }

    public String getPlaceId() {
        return getString(KEY_PLACE_ID);
    }

    public void setPlaceId(String placeId){
        put(KEY_PLACE_ID, placeId);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name){
        put(KEY_NAME, name);
    }

    public String getAddress() {
        return getString(KEY_ADDRESS);
    }

    public void setAddress(String address){
        put(KEY_ADDRESS, address);
    }

    public String getWebsite() {
        return getString(KEY_WEBSITE);
    }

    public void setWebsite(String website){
        put(KEY_WEBSITE, website);
    }

    public String getImageURL() {
        return getString(KEY_IMAGE_URL);
    }

    public void setImageURL(String imageURL){
        put(KEY_IMAGE_URL, imageURL);
    }

    public String getPhoneNumber() {
        return getString(KEY_PHONE_NUMBER);
    }

    public void setPhoneNumber(String phoneNumber){
        put(KEY_PHONE_NUMBER, phoneNumber);
    }

    public double getRating() {
        return getDouble(KEY_RATING);
    }

    public void setRating(double rating){
        put(KEY_RATING, rating);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    public void setLocation(ParseGeoPoint parseGeoPoint){
        put(KEY_LOCATION, parseGeoPoint);
    }
}