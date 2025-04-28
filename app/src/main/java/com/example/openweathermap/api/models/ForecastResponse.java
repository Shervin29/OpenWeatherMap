package com.example.openweathermap.api.models;
import com.google.gson.annotations.SerializedName;
import java.util.List;


public class ForecastResponse {


    @SerializedName("cod")
    private String cod;

    // Internal parameter - might be message count or other value
    @SerializedName("message")
    private Double message; // Often 0 or a numeric code on success

    @SerializedName("cnt")
    private Integer cnt; // Number of forecast items returned in the list

    @SerializedName("list")
    private List<ForecastItem> list; // The list of forecast items

    @SerializedName("city")
    private City city; // Information about the city

    // --- Getters ---
    public String getCod() {
        return cod;
    }

    public Double getMessage() {
        return message;
    }

    public Integer getCnt() {
        return cnt;
    }

    public List<ForecastItem> getList() {
        return list;
    }

    public City getCity() {
        return city;
    }

    // Optional: equals, hashCode, toString
}
