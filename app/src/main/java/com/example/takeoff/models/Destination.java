package com.example.takeoff.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import org.parceler.Parcel;

import java.util.List;

/**
 * Destination:
 * - class for destination data derived from Google AutoComplete SDK
 */
@Parcel(Parcel.Serialization.BEAN)
@ParseClassName("Destination")
public class Destination extends ParseObject{

    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_TYPES = "types";
    public static final String KEY_WEBSITE = "websiteURI";
    public static final String KEY_RATING = "rating";
    public static final String KEY_PRICE_LEVEL = "priceLevel";

    // empty constructor needed by the Parceler library
    public Destination() {
    }

    public String getName(){
        return getString(KEY_NAME);
    }

    public void setName(String name){
        put(KEY_NAME, name);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    public void setLocation(ParseGeoPoint parseGeoPoint) {
        put(KEY_LOCATION, parseGeoPoint);
    }

    public String getAddress(){
        return getString(KEY_ADDRESS);
    }

    public void setAddress(String address){
        put(KEY_ADDRESS, address);
    }

    public List<String> getTypes(){
        return getList(KEY_TYPES);
    }

    public void setTypes(List<String> types){
        put(KEY_TYPES, types);
    }

    public String getWebsite() {
        return getString(KEY_WEBSITE);
    }

    public void setWebsite(String website){
        put(KEY_WEBSITE, website);
    }

    public double getRating(){
        return getDouble(KEY_RATING);
    }

    public void setRating(double rating){
        put(KEY_RATING, rating);
    }

    public int getPriceLevel(){
        return getInt(KEY_PRICE_LEVEL);
    }

    public void setPriceLevel(int priceLevel){
        put(KEY_PRICE_LEVEL, priceLevel);
    }
}