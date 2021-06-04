package com.darrengansberg.restaurantapp;
/*==============ViewOnMap.java==============================
Description: The ViewOnMap class defines an activity
class that provides a UI controller to allow the user to view
a restaurants location on a Google Map. To view the location on a
map the activity makes use of the Google Maps SDK for Android.

Produced by: Darren Gansberg
Copyright: 2021, All Rights Reserved.

 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.darrengansberg.restaurantapp.Util.RestaurantUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

public class ViewOnMap extends AppCompatActivity implements OnMapReadyCallback {

    private LatLng location;
    private String name = "Unnamed";
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_on_map);

        if (savedInstanceState != null)
        {
            double latitude = savedInstanceState.getDouble(RestaurantUtil.RESTAURANT_LOCATION_LAT,
                    RestaurantUtil.INVALID_LAT);
            double longitude = savedInstanceState.getDouble(RestaurantUtil.RESTAURANT_LOCATION_LNG,
                    RestaurantUtil.INVALID_LNG);
            location = new LatLng(latitude, longitude);
            name = savedInstanceState.getString(RestaurantUtil.RESTAURANT_NAME, "Unnamed");

        } else {

            Intent intent = getIntent();
            double latitude = intent.getDoubleExtra(RestaurantUtil.RESTAURANT_LOCATION_LAT, RestaurantUtil.INVALID_LAT);
            double longitude = intent.getDoubleExtra(RestaurantUtil.RESTAURANT_LOCATION_LNG,RestaurantUtil.INVALID_LNG);
            //TODO What about invalid!
            location = new LatLng(latitude, longitude);
            name = intent.getStringExtra(RestaurantUtil.RESTAURANT_NAME);
            if (name == null)
            {
                name = "Unnamed";
            }
        }


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull @org.jetbrains.annotations.NotNull GoogleMap googleMap) {

        //TODO: invalid lat and lng
        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title(name));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,16.0f));
    }

    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(RestaurantUtil.RESTAURANT_LOCATION_LAT, location.latitude);
        outState.putDouble(RestaurantUtil.RESTAURANT_LOCATION_LNG, location.longitude);
        outState.putString(RestaurantUtil.RESTAURANT_NAME, name);
    }

}