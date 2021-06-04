package com.darrengansberg.restaurantapp;
/*==============ViewSavedRestaurants.java==============================
Description: The ViewSavedRestaurants class defines an activity
class that provides a UI controller to allow the user to view
all restaurants that they have saved, whilst using the app,
on a Google Map. To view the location of restaurants on a map
the activity makes use of the Google Maps SDK for Android.

Produced by: Darren Gansberg
Copyright: 2021, All Rights Reserved.

 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.darrengansberg.restaurantapp.Util.RestaurantUtil;
import com.darrengansberg.restaurantapp.data.DatabaseHelper;
import com.darrengansberg.restaurantapp.models.Restaurant;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ViewSavedRestaurants extends AppCompatActivity implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private List<Restaurant> restaurantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_saved_restaurants);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        DatabaseHelper helper = new DatabaseHelper(this, RestaurantUtil.DATABASE_NAME,null,
                RestaurantUtil.DATABASE_VERSION);
        restaurantList = helper.getAllRestaurants();

    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {

        if (restaurantList != null)
        {
            for(int i = 0; i < restaurantList.size(); i++)
            {
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(restaurantList.get(i).getLatitude(),
                                restaurantList.get(i).getLongitude()))
                        .title(restaurantList.get(i).getName()));
            }

            //position the camera to the first saved restaurant.
            if (restaurantList.size() > 0){
                LatLng target = new LatLng(restaurantList.get(0).getLatitude(),
                        restaurantList.get(0).getLongitude());
                CameraPosition position = new CameraPosition(target, 18.0f, 0.0f,
                        0.0f);
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
            }

        }

    }

}