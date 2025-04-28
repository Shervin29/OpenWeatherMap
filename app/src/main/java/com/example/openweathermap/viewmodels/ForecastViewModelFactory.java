package com.example.openweathermap.viewmodels;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ForecastViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public ForecastViewModelFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ForecastViewModel.class)) {
            return (T) new ForecastViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
