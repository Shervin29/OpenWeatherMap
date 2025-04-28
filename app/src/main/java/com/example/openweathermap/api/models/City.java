package com.example.openweathermap.api.models;
import com.google.gson.annotations.SerializedName;

// Represents the 'city' object in the forecast response
public class City {

    @SerializedName("id")
    private Integer id; // City ID

    @SerializedName("name")
    private String name; // City name

    @SerializedName("coord")
    private Coord coord; // Reuses Coord POJO (latitude, longitude)

    @SerializedName("country")
    private String country; // Country code (e.g., "US", "GB")

    @SerializedName("population")
    private Integer population;

    @SerializedName("timezone")
    private Integer timezone; // Shift in seconds from UTC

    @SerializedName("sunrise")
    private Long sunrise; // Sunrise time, unix, UTC

    @SerializedName("sunset")
    private Long sunset; // Sunset time, unix, UTC

    // --- Getters ---
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coord getCoord() {
        return coord;
    }

    public String getCountry() {
        return country;
    }

    public Integer getPopulation() {
        return population;
    }

    public Integer getTimezone() {
        return timezone;
    }

    public Long getSunrise() {
        return sunrise;
    }

    public Long getSunset() {
        return sunset;
    }


}
