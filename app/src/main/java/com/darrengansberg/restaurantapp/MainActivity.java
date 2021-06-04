package com.darrengansberg.restaurantapp;
/*==============MainActivity.java==============================
Description: The MainActivity class defines an activity
class that provides a UI controller to allow which acts
as the main/home starting point for the restaurant app.

Produced by: Darren Gansberg
Copyright: 2021, All Rights Reserved.

 */
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onAddNewPlace(View view)
    {
        Intent intent = new Intent(this, AddRestaurant.class);
        startActivity(intent);
    }

    public void onShowAllOnMap(View view)
    {
        Intent intent = new Intent(this,ViewSavedRestaurants.class);
        startActivity(intent);
    }
}