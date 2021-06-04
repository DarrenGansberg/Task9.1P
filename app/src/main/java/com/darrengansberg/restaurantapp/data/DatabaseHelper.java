package com.darrengansberg.restaurantapp.data;
/*==============DatabaseHelper.java==============================
Description: The DatabaseHelper provides data persistence
for the Restaurant app. It enables the app to persist the details
of a Restaurant, as well as retrieve a restaurants details once
persisted.

Produced by: Darren Gansberg
Copyright: 2021, All Rights Reserved.

 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.darrengansberg.restaurantapp.Util.RestaurantUtil;
import com.darrengansberg.restaurantapp.models.Restaurant;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Vector;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    public Restaurant getRestaurant(@NotNull @NonNull String googlePlaceId)
    {
        Restaurant result = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(true, RestaurantUtil.LOCATIONS_TABLE,
                new String[]{ RestaurantUtil.RESTAURANT_GOOGLE_PLACE_ID },
                RestaurantUtil.RESTAURANT_GOOGLE_PLACE_ID + "=?",
                new String[]{googlePlaceId},null, null, null, null);
        int numOfRows = cursor.getCount();
        if (numOfRows > 1)
        {
            if(cursor.moveToFirst()) {
                result = new Restaurant();
                result.setName(cursor.getString(cursor.getColumnIndex(RestaurantUtil.RESTAURANT_GOOGLE_PLACE_ID)));
            }
        }
        db.close();
        return result;
    }

    public List<Restaurant> getAllRestaurants()
    {
        List<Restaurant> result = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(RestaurantUtil.LOCATIONS_TABLE,
                new String[]{ RestaurantUtil.RESTAURANT_GOOGLE_PLACE_ID,
                RestaurantUtil.RESTAURANT_NAME, RestaurantUtil.RESTAURANT_LOCATION_LAT,
                RestaurantUtil.RESTAURANT_LOCATION_LNG}, null, null,
                null,null,null);
        int rows = cursor.getCount();
        if (rows > 0)
        {
            result = new Vector<Restaurant>(rows);
            Restaurant r;
            while(cursor.moveToNext())
            {
                r = new Restaurant();
                r.setGooglePlaceId(cursor.getString(cursor.getColumnIndex(RestaurantUtil.RESTAURANT_GOOGLE_PLACE_ID)));
                r.setName(cursor.getString(cursor.getColumnIndex(RestaurantUtil.RESTAURANT_NAME)));
                r.setLatitude(cursor.getDouble(cursor.getColumnIndex(RestaurantUtil.RESTAURANT_LOCATION_LAT)));
                r.setLongitude(cursor.getDouble(cursor.getColumnIndex(RestaurantUtil.RESTAURANT_LOCATION_LNG)));
                result.add(r);
            }
        } else {
            result = new Vector<Restaurant>();
        }
        db.close();
        return result;
    }

    public long insertRestaurant(@NotNull @NonNull Restaurant restaurant)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RestaurantUtil.RESTAURANT_GOOGLE_PLACE_ID, restaurant.getGooglePlaceId());
        values.put(RestaurantUtil.RESTAURANT_NAME, restaurant.getName());
        values.put(RestaurantUtil.RESTAURANT_LOCATION_LAT, restaurant.getLatitude());
        values.put(RestaurantUtil.RESTAURANT_LOCATION_LNG, restaurant.getLongitude());
        long newRowId = db.insert(RestaurantUtil.LOCATIONS_TABLE, null, values);
        db.close();
        return newRowId;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + RestaurantUtil.LOCATIONS_TABLE + " (" +
                RestaurantUtil.RESTAURANT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RestaurantUtil.RESTAURANT_GOOGLE_PLACE_ID + " TEXT," +
                RestaurantUtil.RESTAURANT_NAME + " TEXT," +
                RestaurantUtil.RESTAURANT_LOCATION_LAT + " NUMERIC," +
                RestaurantUtil.RESTAURANT_LOCATION_LNG + " NUMERIC" + ")";

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String query = "DROP TABLE IF EXISTS";
        db.execSQL(query, new String[]{ RestaurantUtil.LOCATIONS_TABLE });
        onCreate(db);
    }

    public boolean placeExists(@NotNull @NonNull String googlePlaceId)
    {
        boolean result = true;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(true, RestaurantUtil.LOCATIONS_TABLE,
                new String[]{ RestaurantUtil.RESTAURANT_GOOGLE_PLACE_ID },
                RestaurantUtil.RESTAURANT_GOOGLE_PLACE_ID + "=?",
                new String[]{googlePlaceId},null, null, null, null);

        int numOfRows = cursor.getCount();
        if (numOfRows == 0) //indicates place not found
        {
           result = false;
        }
        db.close();
        return result;
    }

    public boolean updateRestaurant(@NotNull @NonNull Restaurant restaurant)
    {
        boolean updated = false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RestaurantUtil.RESTAURANT_NAME, restaurant.getName());
        values.put(RestaurantUtil.RESTAURANT_LOCATION_LAT, restaurant.getLatitude());
        values.put(RestaurantUtil.RESTAURANT_LOCATION_LNG, restaurant.getLongitude());
        int rowsUpdated = db.update(RestaurantUtil.LOCATIONS_TABLE,values,
                RestaurantUtil.RESTAURANT_GOOGLE_PLACE_ID + "=?",
                new String[]{restaurant.getGooglePlaceId()});
        if (rowsUpdated > 0)
        {
            updated = true;
        }
        db.close();
        return updated;
    }





}
