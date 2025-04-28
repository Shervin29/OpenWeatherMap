package com.example.openweathermap.repositories;

import android.content.Context;

import com.example.openweathermap.api.OpenWeatherMapApi;
import com.example.openweathermap.api.RetrofitClient;
import com.example.openweathermap.api.models.CurrentWeatherResponse;
import com.example.openweathermap.api.models.ForecastResponse;
import com.example.openweathermap.R;

import retrofit2.Call;

public class WeatherRepository {

    private final OpenWeatherMapApi apiService;
    private final String apiKey;
    private final String units = "metric"; // Metric units (Celsius)
    private final int forecastCount = 40;  // 40 readings (~5 days hourly)

    public WeatherRepository(Context context) {
        this.apiService = RetrofitClient.getApiService();
        this.apiKey = context.getString(R.string.openweathermap_api_key);
    }

    // Constructor for testing with mock service
    public WeatherRepository(OpenWeatherMapApi apiService) {
        this.apiService = apiService;
        this.apiKey = ""; // or mock key for testing
    }

    public Call<CurrentWeatherResponse> getCurrentWeather(double lat, double lon) {
        return apiService.getCurrentWeather(lat, lon, apiKey, units);
    }

    public Call<ForecastResponse> getForecast(double lat, double lon) {
        return apiService.getForecast(lat, lon, apiKey, units, forecastCount);
    }
}
