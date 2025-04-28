package com.example.openweathermap.api;

import com.example.openweathermap.api.models.CurrentWeatherResponse;
import com.example.openweathermap.api.models.ForecastResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherMapApi {

    @GET("weather") 
    Call<CurrentWeatherResponse> getCurrentWeather(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey,
            @Query("units") String units
    );

    @GET("forecast")
    Call<ForecastResponse> getForecast(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("cnt") int count
    );
}
