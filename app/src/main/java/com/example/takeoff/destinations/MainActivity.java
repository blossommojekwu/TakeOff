package com.example.takeoff.destinations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.takeoff.R;
import com.example.takeoff.databinding.ActivityMainBinding;
import com.example.takeoff.map.DestinationMapFragment;
import com.example.takeoff.models.Destination;
import com.example.takeoff.models.Hotel;
import com.example.takeoff.plan.PlanFragment;
import com.example.takeoff.stay.StayFragment;
import com.example.takeoff.visitplace.VisitPlaceFragment;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Headers;

import static com.example.takeoff.R.string.google_places_api_key;

/**
 * MainActivity contains:
 * - bottom navigation view that switches to different fragments based on menu icons
 * - search icon that launches Google Places autocomplete activity and returns destination clicked on to parse server
 * - ability to save Destination in parse server to populate feed of destinations
 * - ability to save Hotels nearby destination from saved destination
*/
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String SEARCH_BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    public static final String DETAIL_BASE_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
    private static final String PHOTO_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    public static final int SEARCH_RADIUS = 5000; //in meters
    private static final String MAX_WIDTH = "300";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private PlacesClient mPlacesClient;
    private Place mPlace;
    private List<String> mPlaceIds;
    private BottomNavigationView mButtomNavigation;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private ParseFile mDestinationPhotoFile;
    private String mDestinationPhotoFileName = "destination_photo.jpg";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.actionSearch:
                onSearchCalled();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        // layout of activity is stored in a special property called root
        View mainView = mainBinding.getRoot();
        setContentView(mainView);

        if (!Places.isInitialized()) {
            // Initialize the SDK
            Places.initialize(getApplicationContext(), getString(R.string.google_places_api_key));
        }
        // Create a new PlacesClient instance
        mPlacesClient = Places.createClient(this);

        mPlaceIds = new ArrayList<>();
        mButtomNavigation = mainBinding.bottomNavigation;

        mButtomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.actionHome: fragment = new DestinationsFragment();
                        Toast.makeText(MainActivity.this, R.string.home, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.actionPlan: fragment = new PlanFragment();
                        Toast.makeText(MainActivity.this, R.string.plan, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.actionMap: fragment = new DestinationMapFragment();
                        Toast.makeText(MainActivity.this, R.string.map, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.actionStay: fragment = new StayFragment();
                        Toast.makeText(MainActivity.this, R.string.stay, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.actionVisit:
                    default: fragment = new VisitPlaceFragment();
                        Toast.makeText(MainActivity.this, R.string.visit, Toast.LENGTH_SHORT).show();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        mButtomNavigation.setSelectedItemId(R.id.actionHome);
    }

    public void onSearchCalled() {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG,
                Place.Field.PHOTO_METADATAS, Place.Field.TYPES, Place.Field.WEBSITE_URI, Place.Field.RATING, Place.Field.PRICE_LEVEL);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mPlace = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + mPlace.getName() + ", " + mPlace.getId() + ", " + mPlace.getLatLng());
                //saveDestination using place
                ParseUser currentUser = ParseUser.getCurrentUser();
                saveDestination(mPlace, currentUser);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //creates list of place_ids for hotels near destination
    //Example request: https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=lodging&key=API_KEY
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void searchNetworkRequest(Destination destination){
        AsyncHttpClient placesSearchClient = new AsyncHttpClient();
        String lat = String.valueOf(destination.getLocation().getLatitude());
        String lng = String.valueOf(destination.getLocation().getLongitude());
        String latLng = lat + "," + lng;
        List<String> endpoints = new ArrayList<>();
        endpoints.add("location=" + latLng);
        endpoints.add("radius=" + String.valueOf(SEARCH_RADIUS));
        endpoints.add("type=" + "lodging");
        endpoints.add("key="+ getString(R.string.google_places_api_key));
        final String HOTEL_URL = SEARCH_BASE_URL + String.join("&", endpoints);
        Log.i(TAG, "HOTEL URL: " + HOTEL_URL);
        placesSearchClient.get(HOTEL_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "Hotel Search onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try{
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    mPlaceIds.addAll(Hotel.placeIdFromJSONArray(results));
                    //send Google Places Details request to get hotel details
                    detailsNetworkRequest(mPlaceIds, destination);
                } catch (JSONException e) {
                    Log.e(TAG, "Hit JSON Exception", e);
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "Hotel Search onFailure");
            }
        });
    }

    //uses populated list of place ids to issue network request for hotel details for each place_id
    //Example request: https://maps.googleapis.com/maps/api/place/details/json?place_id=ChIJaQfLwlQrDogRg4iiPfEl1J4&key=API_KEY
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void detailsNetworkRequest(List<String> placeIds, Destination destination){
        AsyncHttpClient placesDetailsClient = new AsyncHttpClient();
        List<String> endpoints = new ArrayList<>();
        for (int i = 0; i < placeIds.size(); i++) {
            endpoints.add("place_id=" + placeIds.get(i));
            endpoints.add("key="+ getString(R.string.google_places_api_key));
            final String HOTEL_DETAILS_URL = DETAIL_BASE_URL + String.join("&", endpoints);
            placesDetailsClient.get(HOTEL_DETAILS_URL, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.d(TAG, "Hotel Details onSuccess");
                    JSONObject jsonObject = json.jsonObject;
                    try {
                        JSONObject result = jsonObject.getJSONObject("result");
                        Log.i(TAG, "Result: " + result.toString());
                        saveHotel(result, destination);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.d(TAG, "Hotel Details onFailure");
                }
            });
            //clear out endpoints for new placeId
            endpoints.clear();
        }
    }

    private void saveHotel(JSONObject jsonObject, ParseObject destination) throws JSONException {
        Hotel hotel = new Hotel();
        hotel.setDestination(destination);
        hotel.setPlaceId(jsonObject.getString("place_id"));
        hotel.setName(jsonObject.getString("name"));
        hotel.setAddress(jsonObject.getString("formatted_address"));
        hotel.setWebsite(jsonObject.getString("website"));
        hotel.setPhoneNumber(jsonObject.getString("formatted_phone_number"));
        if (jsonObject.has("rating")){
            hotel.setRating(jsonObject.getDouble("rating"));
        }
        String photoReference = jsonObject.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
        String HOTEL_PHOTO_URL = PHOTO_BASE_URL + "maxwidth=" + MAX_WIDTH + "&photoreference="
                + photoReference + "&key=" + getString(google_places_api_key);
        hotel.setImageURL(HOTEL_PHOTO_URL);
        double lat = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
        double lng = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
        ParseGeoPoint location = new ParseGeoPoint();
        location.setLatitude(lat);
        location.setLongitude(lng);
        hotel.setLocation(location);
        hotel.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(MainActivity.this, R.string.saving_error, Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save was successful!");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveDestination(Place place, ParseUser currentUser) {
        Destination destination = new Destination();
        setPhoto(destination);
        destination.setName(place.getName());
        destination.setDescription(place.getAddress());
        destination.setAddress(place.getAddress());
        if (place.getLatLng() != null) {
            ParseGeoPoint location = new ParseGeoPoint();
            location.setLatitude(place.getLatLng().latitude);
            location.setLongitude(place.getLatLng().longitude);
            destination.setLocation(location);
        }
        destination.setUser(currentUser);
        List<String> types = new ArrayList<>();
        for (Place.Type type : place.getTypes()){
            types.add(type.toString());
        }
        destination.setTypes(types);
        if (place.getRating() != null) destination.setRating(place.getRating());
        if (place.getPriceLevel() != null) destination.setPriceLevel(place.getPriceLevel());
        if (place.getWebsiteUri() != null) destination.setWebsite(place.getWebsiteUri().toString());
        destination.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(MainActivity.this, R.string.saving_error, Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Destination save was successful!");
            }
        });
        //Send Network Request for Hotel info
        searchNetworkRequest(destination);
    }

    private void setPhoto(Destination destination){
        // Get the photo metadata.
        final List<PhotoMetadata> metadata = mPlace.getPhotoMetadatas();
        if (metadata == null || metadata.isEmpty()) {
            Log.w(TAG, "No photo metadata.");
            return;
        }
        final PhotoMetadata photoMetadata = metadata.get(0);

        // Get the attribution text.
        final String attributions = photoMetadata.getAttributions();

        // Create a FetchPhotoRequest.
        final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .build();
        mPlacesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
            Bitmap bitmap = fetchPhotoResponse.getBitmap();
            if (bitmap == null){
                Log.e(TAG, "Bitmap is NULL");
            } else {
                mDestinationPhotoFile = convertBitmapToParsefile(bitmap, mDestinationPhotoFileName);
                Log.i(TAG, "Photo File STATUS: " + mDestinationPhotoFile.isDataAvailable());
                destination.setImage(mDestinationPhotoFile);
                destination.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.e(TAG, "Photo saved in Destination", e);
                    }
                });
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
            }
        });
    }

    public ParseFile convertBitmapToParsefile(Bitmap bitMap, String fileName){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG,100,bos);
        byte[] imageByte = bos.toByteArray();
        ParseFile parseFile = new ParseFile(fileName,imageByte);
        return parseFile;
    }
}