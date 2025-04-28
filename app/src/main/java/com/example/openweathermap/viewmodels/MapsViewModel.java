package com.example.openweathermap.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.location.Location;
import android.util.Log; // For logging

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.example.openweathermap.ResultWrapper; // Import wrapper
import com.example.openweathermap.api.models.CurrentWeatherResponse;
import com.example.openweathermap.repositories.WeatherRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsViewModel extends AndroidViewModel {

    private static final String TAG = "MapsViewModel"; // For logging

    private final WeatherRepository weatherRepository;
    private final FusedLocationProviderClient fusedLocationClient;

    private final MutableLiveData<ResultWrapper<Location>> _currentLocation = new MutableLiveData<>();
    public final LiveData<ResultWrapper<Location>> currentLocation = _currentLocation;

    private final MutableLiveData<ResultWrapper<CurrentWeatherResponse>> _tappedLocationWeather = new MutableLiveData<>();
    public final LiveData<ResultWrapper<CurrentWeatherResponse>> tappedLocationWeather = _tappedLocationWeather;


    public MapsViewModel(@NonNull Application application) {
        super(application);
        weatherRepository = new WeatherRepository(application.getApplicationContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(application.getApplicationContext());
    }


    @SuppressLint("MissingPermission") // Ensure permission is checked before calling
    public void fetchCurrentLocation() {
        _currentLocation.setValue(ResultWrapper.loading()); // Post Loading state
        // Request current location using the Task API
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationTokenSource().getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        _currentLocation.setValue(ResultWrapper.success(location));
                    } else {
                        _currentLocation.setValue(ResultWrapper.error("Unable to get current location (result is null).", null));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching location", e);
                    if (e instanceof SecurityException) {
                        _currentLocation.setValue(ResultWrapper.error("Location permission denied.", e));
                    } else {
                        _currentLocation.setValue(ResultWrapper.error("Error fetching location: " + e.getMessage(), e));
                    }
                });
    }

    public void fetchWeatherForCoordinates(LatLng latLng) {
        _tappedLocationWeather.setValue(ResultWrapper.loading());
        Call<CurrentWeatherResponse> call = weatherRepository.getCurrentWeather(latLng.latitude, latLng.longitude);

        // Execute the call asynchronously
        call.enqueue(new Callback<CurrentWeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<CurrentWeatherResponse> call, @NonNull Response<CurrentWeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _tappedLocationWeather.setValue(ResultWrapper.success(response.body()));
                } else {
                    String errorMsg = "API Error: " + response.code() + " " + response.message();
                    try { // Try to read error body
                        if(response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) { Log.e(TAG, "Error reading error body", e); }
                    _tappedLocationWeather.setValue(ResultWrapper.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CurrentWeatherResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Network failure fetching weather", t);
                _tappedLocationWeather.setValue(ResultWrapper.error("Network Error: " + t.getMessage(), t));
            }
        });
    }
}
