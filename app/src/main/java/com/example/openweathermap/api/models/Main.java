package com.example.openweathermap.api.models;

import com.google.gson.annotations.SerializedName;

public class Main {
    @SerializedName("temp")
    private Double temp;
    @SerializedName("feels_like")
    private Double feelsLike;
    @SerializedName("temp_min")
    private Double tempMin;
    @SerializedName("temp_max")
    private Double tempMax;
    @SerializedName("pressure")
    private Integer pressure;
    @SerializedName("humidity")
    private Integer humidity;

    // Getters...
    public Double getTemp() { return temp; }
    // ... other getters
}
