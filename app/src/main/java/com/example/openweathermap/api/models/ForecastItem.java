package com.example.openweathermap.api.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Objects; // Needed if implementing equals/hashCode

// Represents a single forecast entry in the 5-day/3-hour list
public class ForecastItem {

    @SerializedName("dt")
    private Long dt; // Time of data forecasted, unix, UTC

    @SerializedName("main")
    private Main main; // Reuses Main POJO (temp, pressure, humidity etc.)

    @SerializedName("weather")
    private List<Weather> weather; // Reuses Weather POJO (main condition, description, icon)

    @SerializedName("clouds")
    private Clouds clouds; // Cloudiness information

    @SerializedName("wind")
    private Wind wind; // Wind information

    @SerializedName("visibility")
    private Integer visibility; // Average visibility, metres

    @SerializedName("pop")
    private Double pop; // Probability of precipitation (0 to 1)



    @SerializedName("sys")
    private Sys sys; // Part of day ('d' or 'n')

    @SerializedName("dt_txt")
    private String dtTxt;

    // --- Getters ---
    public Long getDt() {
        return dt;
    }

    public Main getMain() {
        return main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public Wind getWind() {
        return wind;
    }

    public Integer getVisibility() {
        return visibility;
    }

    public Double getPop() {
        return pop;
    }

    public Sys getSys() {
        return sys;
    }

    public String getDtTxt() {
        return dtTxt;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForecastItem that = (ForecastItem) o;
        // Compare relevant fields used for display or identity
        return Objects.equals(dt, that.dt) &&
                Objects.equals(main, that.main) && // Requires Main.equals()
                Objects.equals(weather, that.weather) && // Requires Weather.equals() and List comparison
                Objects.equals(dtTxt, that.dtTxt); // Add other fields if needed for comparison
        // Comparing nested objects requires those objects to also implement equals()
    }

    @Override
    public int hashCode() {
        // Generate hashCode based on the same fields used in equals()
        return Objects.hash(dt, main, weather, dtTxt);
    }

    // Optional: toString() for debugging
}
