package com.example.openweathermap.api.models;

import com.google.gson.annotations.SerializedName;


public class Sys {

    @SerializedName("pod")
    private String pod;

    // Getter
    public String getPod() {
        return pod;
    }


}
