package com.example.openweathermap.api.models;
import com.google.gson.annotations.SerializedName;

public class Weather {
    @SerializedName("id")
    private Integer id;
    @SerializedName("main")
    private String main;
    @SerializedName("description")
    private String description;
    @SerializedName("icon")
    private String icon;

    // Getters...
    public Integer getId() { return id; }
    public String getMain() { return main; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
}
