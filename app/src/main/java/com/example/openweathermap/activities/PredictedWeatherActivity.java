package com.example.openweathermap.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.openweathermap.R;
import com.example.openweathermap.fragments.PredictedWeatherFragment;

public class PredictedWeatherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predicted_weather);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.predicted_container, new PredictedWeatherFragment())
                    .commit();
        }
    }
}
