package com.example.takeoff.visitplace;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.takeoff.R;
import com.example.takeoff.destinations.MainActivity;
import com.example.takeoff.models.Destination;
import com.example.takeoff.models.VisitPlace;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

import static com.example.takeoff.R.string.google_places_api_key;

/**
 * VisitPlaceFragment:
 * - contains Google Places Search API call to retrieve data for Visit Place
 */
public class VisitPlaceFragment extends Fragment {

    public static final String TAG = "VisitPlaceFragment";
    public static final String SEARCH_BASE_URL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?";
    private static final String PHOTO_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    private static final String MAX_WIDTH = "300";
    private Destination mCurrentDestination;
    private List<String> mVisitPlacesItems;
    private ExtendedFloatingActionButton mFloatingActionBtn;
    private RecyclerView mRvVisitPlaces;
    private TextInputEditText mEtVisitPlaceText;
    List<VisitPlace> mVisitPlaces;

    public VisitPlaceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visit_place, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.getArguments() != null){
            mCurrentDestination = (Destination) Parcels.unwrap(this.getArguments().getParcelable("current destination"));
            Log.i(TAG, "CURRENT DEST: " + mCurrentDestination.getName());
        }
        mVisitPlacesItems = new ArrayList<>();
        mFloatingActionBtn = view.findViewById(R.id.fabAddPlace);
        mRvVisitPlaces = view.findViewById(R.id.rvVisitPlaces);
        mEtVisitPlaceText = view.findViewById(R.id.etVisitPlaceText);
        mFloatingActionBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Added Place", Toast.LENGTH_SHORT).show();
                //save text in edit text when button is clicked
                String placeItem = mEtVisitPlaceText.getText().toString();
                mVisitPlacesItems.add(placeItem);
                //issue network request call for Google Places Search to save Visit Place
                searchVisitPlace(mCurrentDestination, placeItem);
            }
        });
    }

    // Example request: https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=Museum%20of%20Contemporary%20Art%20Australia&inputtype=textquery&fields=photos,formatted_address,name,rating,opening_hours,geometry&key=YOUR_API_KEY
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void searchVisitPlace(Destination destination, String placeText){
        AsyncHttpClient placesSearchClient = new AsyncHttpClient();
        String destinationText = destination.getAddress().replaceAll(" ", "%20");
        String inputText = placeText.replaceAll(" ", "%20");
        String input = inputText + "%20" + destinationText;
        List<String> endpoints = new ArrayList<>();
        endpoints.add("input=" + input);
        endpoints.add("inputtype=textquery");
        endpoints.add("fields=name,formatted_address,photos,geometry");
        endpoints.add("key="+ getString(R.string.google_places_api_key));
        final String VISIT_PLACE_URL = SEARCH_BASE_URL + String.join("&", endpoints);
        Log.i(TAG, "VISIT PLACE URL: " + VISIT_PLACE_URL);
        placesSearchClient.get(VISIT_PLACE_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "VisitPlace Search onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try{
                    JSONArray candidates = jsonObject.getJSONArray("candidates");
                    JSONObject topCandidate = (JSONObject) candidates.get(0);
                    saveVisitPlace(topCandidate, destination);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "VisitPlace Search onFailure");
            }
        });
    }

    private void saveVisitPlace(JSONObject topCandidate, Destination destination) throws JSONException {
        VisitPlace visitPlace = new VisitPlace();
        visitPlace.setDestination(destination);
        visitPlace.setName(topCandidate.getString("name"));
        visitPlace.setAddress(topCandidate.getString("formatted_address"));
        double lat = topCandidate.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
        double lng = topCandidate.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
        ParseGeoPoint location = new ParseGeoPoint();
        location.setLatitude(lat);
        location.setLongitude(lng);
        visitPlace.setLocation(location);
        String photoReference = topCandidate.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
        String VISIT_PLACE_PHOTO_URL = PHOTO_BASE_URL + "maxwidth=" + MAX_WIDTH + "&photoreference="
                + photoReference + "&key=" + getString(google_places_api_key);
        visitPlace.setPhotoURL(VISIT_PLACE_PHOTO_URL);
        visitPlace.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), R.string.saving_error, Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Visit Place save was successful!");
            }
        });
    }
}