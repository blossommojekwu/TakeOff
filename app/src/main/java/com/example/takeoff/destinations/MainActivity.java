package com.example.takeoff.destinations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.takeoff.R;
import com.example.takeoff.databinding.ActivityMainBinding;
import com.example.takeoff.models.Destination;
import com.example.takeoff.plan.PlanFragment;
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
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* MainActivity contains:
* - bottom navigation view that switches to different fragments based on menu icons
* - search icon that launches Google Places autocomplete activity and returns destination clicked on
*   to parse server
*/

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    PlacesClient placesClient;
    Place place;
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
        placesClient = Places.createClient(this);

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
                    case R.id.actionStay:
                        Toast.makeText(MainActivity.this, R.string.stay, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.actionVisit:
                    default:
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());
                //saveDestination using place
                ParseUser currentUser = ParseUser.getCurrentUser();
                saveDestination(place, currentUser);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

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
            System.out.println("Type: " + type.toString());
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
                Log.i(TAG, "Post save was successful!");
            }
        });
    }

    private void setPhoto(Destination destination){
        // Get the photo metadata.
        final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
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
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
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