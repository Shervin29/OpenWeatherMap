package com.example.openweathermap.api.models;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CurrentWeatherResponse {
    @SerializedName("coord")
    private Coord coord;
    @SerializedName("weather")
    private List<Weather> weather;
    @SerializedName("main")
    private Main main;
    @SerializedName("name")
    private String name;
    @SerializedName("dt")
    private Long dt; // Timestamp


    public Coord getCoord() { return coord; }
    public List<Weather> getWeather() { return weather; }
    public Main getMain() { return main; }
    public String getName() { return name; }
    public Long getDt() { return dt; }
}
