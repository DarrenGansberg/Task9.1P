package com.darrengansberg.restaurantapp;
/*==============SearchForRestaurant.java==============================
Description: The SearchForRestaurant class defines an activity
class that provides a UI controller to allow the user to
search for a restaurant. The Activity utilises the AutoCompleteSupport
fragment from the Google Places SDK for Android to find a place.

Produced by: Darren Gansberg
Copyright: 2021, All Rights Reserved.

 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.darrengansberg.restaurantapp.Util.RestaurantUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class SearchForRestaurant extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_restaurant);

        Places.initialize(this, getString(R.string.google_api_key));
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,
                Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                Intent result = new Intent();
                result.putExtra(RestaurantUtil.RESTAURANT_GOOGLE_PLACE_ID,place.getId());
                result.putExtra(RestaurantUtil.RESTAURANT_NAME, place.getName());
                result.putExtra(RestaurantUtil.RESTAURANT_ADDRESS, place.getAddress());
                result.putExtra(RestaurantUtil.RESTAURANT_LOCATION_LAT, place.getLatLng().latitude);
                result.putExtra(RestaurantUtil.RESTAURANT_LOCATION_LNG, place.getLatLng().longitude);
                setResult(RestaurantUtil.LOCATION_SEARCH,result);
                finish();
            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                //Log.i(TAG, "An error occurred: " + status);
            }
        });


    }
}