package com.darrengansberg.restaurantapp.Util;
/*==============RestaurantUtil.java==============================
Description: The RestaurantUtil class defines/provides utility
data, that is used throughout the Restaurant app, including
information for the structure of its SQL Lite database.

Produced by: Darren Gansberg
Copyright: 2021, All Rights Reserved.

 */
public class RestaurantUtil {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "restaurant_db";
    public static final String LOCATIONS_TABLE = "locations";
    public static final String RESTAURANT_ADDRESS = "restaurant_address";
    public static final String RESTAURANT_ID = "restaurant_id";
    public static final String RESTAURANT_GOOGLE_PLACE_ID = "restaurant_google_place_id";
    public static final String RESTAURANT_LOCATION_LAT = "restaurant_lat";
    public static final String RESTAURANT_LOCATION_LNG = "restaurant_lng";
    public static final String RESTAURANT_NAME = "restaurant_name";
    public static final Double INVALID_LAT = -180.0;
    public static final Double INVALID_LNG = -270.0;
    public static final String LOCATION_TEXTVIEW = "location_textview";
    public static final int LOCATION_SEARCH = 1;
}