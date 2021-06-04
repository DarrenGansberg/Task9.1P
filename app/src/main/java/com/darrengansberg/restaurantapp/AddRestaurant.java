package com.darrengansberg.restaurantapp;
/*==============AddRestaurant.java==============================
Description: The AddRestaurant class defines an activity
class that provides a UI controller to allow a user to
to add a restaurant to the restaurant app.

Produced by: Darren Gansberg
Copyright: 2021, All Rights Reserved.

 */
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import com.darrengansberg.restaurantapp.Util.RestaurantUtil;
import com.darrengansberg.restaurantapp.data.DatabaseHelper;
import com.darrengansberg.restaurantapp.models.Restaurant;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Vector;

public class AddRestaurant extends AppCompatActivity {

    private ActivityResultLauncher<ActivityResultContracts.RequestPermission> ActivityLauncher;
    private LocationManager locationManager;
    private Restaurant restaurant;


    private ActivityResultLauncher<String> l;
    ActivityResultLauncher<Intent> autoCompleteLauncher;

    private static final int LOCATION_PERMISSION_REQUEST = 1;

    private void displayError(@NotNull Context context, @NotNull String title, @NotNull String message)
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }
        );
        AlertDialog dialog = alertBuilder.show();
    }

    private void displayErrorOnSave()
    {
        displayError(this, getString(R.string.save_error_title),
                getString(R.string.save_error_msg));

    }

    private void displayGooglePlacesError()
    {
        displayError(this, getString(R.string.google_place_retrieval_error_title),
               getString(R.string.google_place_retrieval_error_msg));
    }

    private void displayLocation() {
        displayLocationName();
        displayLocationAddress();

    }

    private void displayLocationName() {
        if (restaurant != null)
        {
            if (restaurant.getName() != null) {
                MaterialTextView locationTxt = findViewById(R.id.location_name_textview);
                locationTxt.setText(restaurant.getName());
            }
        }
    }

    private void displayLocationAddress() {
        if (restaurant != null) {

            if (restaurant.getAddress() != null){
                MaterialTextView nameTxt = findViewById(R.id.location_address_textview);
                nameTxt.setText(restaurant.getAddress());
            }

        }
    }

    private void displayLocationPermissionError()
    {
        MaterialAlertDialogBuilder alertBuilder = new MaterialAlertDialogBuilder(this);
        alertBuilder.setTitle(R.string.location_permission_error_title);
        alertBuilder.setMessage(R.string.location_permission_error_msg);
        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertBuilder.show();
    }

    private void displayNoLocationError(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.no_location_error_title);
        builder.setMessage(R.string.no_location_error_msg);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void getLocation() {

        //Check ACCESS_FINE_LOCATION_PERMISSION has been granted, and if not granted request
        //required permission.
        int accessFineLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if ((accessFineLocation != PackageManager.PERMISSION_GRANTED)) {

            getLocationPermissions();
            return;
        }

        //Check that Location services is available and enabled on the device.

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(LocationManager.class);

            if (locationManager == null) {
                displayError(this,
                        getString(R.string.location_service_unavailable_title),
                        getString(R.string.location_service_unavailable_msg));
                return;
            } else {
                List<String> providers = locationManager.getAllProviders();
                boolean gpsProviderEnabled = false;
                boolean networkProviderEnabled = false;
                for (int i = 0; i < providers.size(); i++) {
                    switch (providers.get(i)) {
                        case "gps":
                            gpsProviderEnabled = locationManager.isProviderEnabled("gps");
                            break;
                        case "network":
                            networkProviderEnabled = locationManager.isProviderEnabled("network");
                            break;
                        default:
                            continue;
                    }
                }

                if ((!gpsProviderEnabled) && (!networkProviderEnabled)) {
                    locationManager = null;
                    displayError(this,
                            getString(R.string.location_service_not_enabled_title),
                            getString(R.string.location_service_not_enabled_msg));
                    return;
                }
            }
        }

        Places.initialize(this, getString(R.string.google_api_key));
        PlacesClient placesClient = Places.createClient(this);
        List<Place.Field> fields = new Vector<>();
        fields.add(Place.Field.ID);
        fields.add(Place.Field.NAME);
        fields.add(Place.Field.LAT_LNG);
        fields.add(Place.Field.ADDRESS);

        FindCurrentPlaceRequest.Builder builder = FindCurrentPlaceRequest.builder(fields);
        placesClient.findCurrentPlace(builder.build())
                .addOnCompleteListener(new GetCurrentLocationCompleted());
    }

    private String getLocationName(){
        MaterialTextView view = findViewById(R.id.location_name_textview);
        return view.getText().toString();
    }

    private String getLocationAddress()
    {
        MaterialTextView view = findViewById(R.id.location_address_textview);
        return view.getText().toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void getLocationPermissions(){

            String[] permissions = new String[]{ Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(permissions, LOCATION_PERMISSION_REQUEST);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);
        if (savedInstanceState != null)
        {
            restaurant = new Restaurant();
            restaurant.setId(savedInstanceState.getInt(RestaurantUtil.RESTAURANT_ID));
            restaurant.setGooglePlaceId(savedInstanceState.getString(RestaurantUtil.RESTAURANT_GOOGLE_PLACE_ID));
            restaurant.setName(savedInstanceState.getString(RestaurantUtil.RESTAURANT_NAME));
            restaurant.setAddress(savedInstanceState.getString(RestaurantUtil.RESTAURANT_ADDRESS));
            restaurant.setLatitude(savedInstanceState.getDouble(RestaurantUtil.RESTAURANT_LOCATION_LAT));
            restaurant.setLongitude(savedInstanceState.getDouble(RestaurantUtil.RESTAURANT_LOCATION_LNG));
        }
        ActivityResultContracts.RequestPermission request = new ActivityResultContracts.RequestPermission();
        ActivityResultCallback<Boolean> callback = new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result == true)
                {
                    getLocation();
                }
                else
                {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getApplicationContext()
                    ,  R.style.Theme_MaterialComponents_DayNight_DarkActionBar_Bridge);
                    builder.setTitle("Grant perms wuss");
                    builder.setMessage("Do it bitch!");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }
            }
        };
        l = registerForActivityResult(request, callback);

        ActivityResultCallback<ActivityResult> autoCompleteCallback = new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RestaurantUtil.LOCATION_SEARCH)
                    {
                        Intent resultData = result.getData();
                        if (result != null)
                        {
                            if (restaurant == null)
                            {
                                restaurant = new Restaurant();
                            }
                            restaurant.setGooglePlaceId(
                                    resultData.getStringExtra(RestaurantUtil.RESTAURANT_GOOGLE_PLACE_ID));
                            restaurant.setName(resultData.getStringExtra(RestaurantUtil.RESTAURANT_NAME));
                            restaurant.setAddress(resultData.getStringExtra(RestaurantUtil.RESTAURANT_ADDRESS));
                            restaurant.setLatitude(resultData.getDoubleExtra(RestaurantUtil.RESTAURANT_LOCATION_LAT,
                                    RestaurantUtil.INVALID_LAT));
                            restaurant.setLongitude(resultData.getDoubleExtra(RestaurantUtil.RESTAURANT_LOCATION_LNG,
                                    RestaurantUtil.INVALID_LNG));
                            displayLocation();
                        } else {
                            displayGooglePlacesError();
                        }
                    }
            }
        };
        autoCompleteLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                autoCompleteCallback);

        displayLocation();
    }

    public void onGetCurrentLocation(View view){
        getLocation();
    }

    public void onLocationClicked(View view)
    {
        if (autoCompleteLauncher != null)
        {
            Intent intent = new Intent(this, SearchForRestaurant.class);
            autoCompleteLauncher.launch(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST)
        {
            boolean isGranted = false;
            for(int i = 0; i < permissions.length; i++)
            {
                switch(permissions[i])
                {
                    case Manifest.permission.ACCESS_FINE_LOCATION:
                    case Manifest.permission.ACCESS_COARSE_LOCATION:
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        {
                            isGranted = true;
                        }
                        break;
                    default:
                        break;
                }
            }

            if (isGranted) {
                getLocation();
            } else {
                displayLocationPermissionError();
            }


        }
    }

    public void onSaveClicked(View view) throws Exception {
        if (restaurant != null)
        {
            DatabaseHelper dbHelper = new DatabaseHelper(this, RestaurantUtil.DATABASE_NAME,
                null, RestaurantUtil.DATABASE_VERSION);

            if (!dbHelper.placeExists(restaurant.getGooglePlaceId()))
            {
                long rowId = dbHelper.insertRestaurant(restaurant);
                if (rowId == -1)
                {
                    displayErrorOnSave();
                    return;
                }
                Toast.makeText(this, "Location saved", Toast.LENGTH_SHORT).show();
            }
            else {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.location_already_exists_title);
                builder.setMessage(R.string.location_already_exists_msg);
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == AlertDialog.BUTTON_POSITIVE)
                        {
                            if(dbHelper.updateRestaurant(restaurant)){
                                Toast.makeText(getApplicationContext(), "Location updated", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                displayErrorOnSave();
                            }
                        }
                    }
                };
                builder.setNegativeButton("No", listener);
                builder.setNeutralButton("Cancel", listener);
                builder.setPositiveButton("Yes", listener);
                builder.show();
            }
        }
        else {

            displayNoLocationError();

        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (restaurant != null)
        {
            outState.putInt(RestaurantUtil.RESTAURANT_ID, restaurant.getId());
            outState.putString(RestaurantUtil.RESTAURANT_GOOGLE_PLACE_ID, restaurant.getGooglePlaceId());
            outState.putString(RestaurantUtil.RESTAURANT_NAME, restaurant.getName());
            outState.putString(RestaurantUtil.RESTAURANT_ADDRESS, restaurant.getAddress());
            outState.putDouble(RestaurantUtil.RESTAURANT_LOCATION_LAT, restaurant.getLatitude());
            outState.putDouble(RestaurantUtil.RESTAURANT_LOCATION_LNG, restaurant.getLongitude());
        }
    }

    public void onShowOnMap(View view){

        if (restaurant != null)
        {
            Intent intent = new Intent(this, ViewOnMap.class);
            intent.putExtra(RestaurantUtil.RESTAURANT_NAME, restaurant.getName());
            intent.putExtra(RestaurantUtil.RESTAURANT_LOCATION_LAT, restaurant.getLatitude());
            intent.putExtra(RestaurantUtil.RESTAURANT_LOCATION_LNG, restaurant.getLongitude());
            startActivity(intent);
        }
        else {
            displayNoLocationError();
        }
    }

    private class GetCurrentLocationCompleted implements OnCompleteListener<FindCurrentPlaceResponse> {

        @Override
        public void onComplete(@NonNull @NotNull Task<FindCurrentPlaceResponse> task) {

            if (task.isSuccessful())
            {
                FindCurrentPlaceResponse result = task.getResult();
                if (result != null)
                {
                    if ((result.getPlaceLikelihoods() != null) && (result.getPlaceLikelihoods().size() > 1))
                    {
                        Place place = result.getPlaceLikelihoods().get(0).getPlace();
                        restaurant = new Restaurant();
                        restaurant.setGooglePlaceId(place.getId());
                        restaurant.setName(place.getName());
                        restaurant.setAddress(place.getAddress());
                        restaurant.setLatitude(place.getLatLng().latitude);
                        restaurant.setLongitude(place.getLatLng().longitude);
                        displayLocation();
                    }
                }
            } else {

                displayGooglePlacesError();
            }
        }
    }


}