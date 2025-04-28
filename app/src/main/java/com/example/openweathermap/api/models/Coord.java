package com.example.openweathermap.api.models;

import com.google.gson.annotations.SerializedName;

public class Coord {
    @SerializedName("lon")
    private Double lon;
    @SerializedName("lat")
    private Double lat;

    // Getters
    public Double getLon() { return lon; }
    public Double getLat() { return lat; }
    // Optional: Setters, Constructor, equals, hashCode, toString
}
