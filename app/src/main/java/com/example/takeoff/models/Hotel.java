package com.example.takeoff.models;

import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Hotel:
 * - class for hotel data model
 */
@ParseClassName("Hotel")
public class Hotel {

    String placeId;
    String name;
    LatLng location;
    double rating;
    String address;
    String phoneNumber;
    String website;
    String photoURL;

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

    public static String getPhotoReference(JSONObject jsonObject) throws JSONException{
        String photoReference = jsonObject.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
        return photoReference;
    }

    public Hotel(JSONObject jsonObject) throws JSONException {
        name = jsonObject.getString("name");
        Log.i("HOTEL: ", name);
        double lat = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
        double lng = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
        location = new LatLng(lat, lng);
        placeId = jsonObject.getString("place_id");
        if (jsonObject.has("rating")){
            rating = jsonObject.getDouble("rating");
        }
        address = jsonObject.getString("formatted_address");
        phoneNumber = jsonObject.getString("formatted_phone_number");
        website = jsonObject.getString("website");
    }

    public void setPhotoURL(String imageURL){
        photoURL = imageURL;
    }
}