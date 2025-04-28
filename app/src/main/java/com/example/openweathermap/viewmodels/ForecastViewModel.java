package com.example.openweathermap.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.openweathermap.ResultWrapper;
import com.example.openweathermap.api.models.ForecastResponse;
import com.example.openweathermap.repositories.WeatherRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastViewModel extends ViewModel {

    private static final String TAG = "ForecastViewModel";

    private final WeatherRepository weatherRepository;
    private final MutableLiveData<ResultWrapper<ForecastResponse>> _forecast = new MutableLiveData<>();
    public final LiveData<ResultWrapper<ForecastResponse>> forecast = _forecast;


    public ForecastViewModel(Context context) {
        this.weatherRepository = new WeatherRepository(context);
    }

    public void fetchForecast(double lat, double lon) {
        _forecast.setValue(ResultWrapper.loading());
        Call<ForecastResponse> call = weatherRepository.getForecast(lat, lon);

        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(@NonNull Call<ForecastResponse> call, @NonNull Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _forecast.setValue(ResultWrapper.success(response.body()));
                } else {
                    String errorMsg = "API Error: " + response.code() + " " + response.message();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    _forecast.setValue(ResultWrapper.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForecastResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Network failure fetching forecast", t);
                _forecast.setValue(ResultWrapper.error("Network Error: " + t.getMessage(), t));
            }
        });
    }
}
