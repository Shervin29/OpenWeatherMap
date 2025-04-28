package com.example.openweathermap.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

public class LocationUtils {


    public static String formatLatLng(LatLng latLng) {
        if (latLng == null) return "Unknown location";
        return String.format(Locale.getDefault(), "Lat: %.4f, Lon: %.4f", latLng.latitude, latLng.longitude);
    }
}
