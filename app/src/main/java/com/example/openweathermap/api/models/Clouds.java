package com.example.openweathermap.api.models;

import com.google.gson.annotations.SerializedName;


public class Clouds {

    @SerializedName("all")
    private Integer all; // Cloudiness percentage

    // Getter
    public Integer getAll() {
        return all;
    }


}
