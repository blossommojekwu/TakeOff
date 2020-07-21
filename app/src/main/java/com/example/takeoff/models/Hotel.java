package com.example.takeoff.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * Hotel:
 * - class for hotel data model
 */
@ParseClassName("Hotel")
public class Hotel extends ParseObject {

    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_WEBSITE = "website";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_RATING = "rating";
    public static final String KEY_PRICE_LEVEL = "priceLevel";
    public static final String KEY_LOCATION = "location";

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

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
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

    public int getPriceLevel() {
        return getInt(KEY_PRICE_LEVEL);
    }

    public void setPriceLevel(int priceLevel){
        put(KEY_PRICE_LEVEL, priceLevel);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    public void setLocation(ParseGeoPoint parseGeoPoint) {
        put(KEY_LOCATION, parseGeoPoint);
    }
}