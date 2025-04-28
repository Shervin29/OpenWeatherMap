package com.example.openweathermap.api.models;

import com.google.gson.annotations.SerializedName;


public class Wind {

    @SerializedName("speed")
    private Double speed; // Wind speed

    @SerializedName("deg")
    private Integer deg; // Wind direction, degrees (meteorological)

    @SerializedName("gust")
    private Double gust; // Wind gust speed (optional, may not always be present)

    // Getters
    public Double getSpeed() {
        return speed;
    }

    public Integer getDeg() {
        return deg;
    }

    public Double getGust() {
        return gust;
    }

    // Optional: equals, hashCode, toString
}
