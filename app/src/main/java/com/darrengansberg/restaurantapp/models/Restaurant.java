package com.darrengansberg.restaurantapp.models;
/*==============Restaurant.java==============================
Description: The Restaurant class is a domain model that
represents a restaurant. It holds data including
the name of the restaurant, the Google Place Id for the
restaurant, and its geo-coordinates.

Produced by: Darren Gansberg
Copyright: 2021, All Rights Reserved.

 */
import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class Restaurant {

    private int id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String googlePlaceId;

    public String getAddress(){ return address; }
    public String getGooglePlaceId(){return googlePlaceId;}
    public int getId(){ return id; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getName(){ return name; }

    public void setAddress(@NotNull @NonNull String address)
    {
        this.address = address;
    }

    public void setGooglePlaceId(@NotNull @NonNull String googlePlaceId)
    {
        this.googlePlaceId = googlePlaceId;
    }

    public void setId(@NonNull int id)
    {
        this.id = id;
    }

    public void setName(@NotNull @NonNull String name)
    {
        this.name = name;
    }

    public void setLatitude(@NotNull @NonNull Double latitude)
    {
        this.latitude = latitude;
    }

    public void setLongitude(@NotNull @NonNull Double longitude)
    {
        this.longitude = longitude;
    }



}
