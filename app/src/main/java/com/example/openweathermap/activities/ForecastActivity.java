package com.example.openweathermap.activities;

import android.os.Bundle;
import android.util.Log; // For logging
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import com.example.openweathermap.viewmodels.ForecastViewModelFactory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager; // Needed if not set in XML

import com.example.openweathermap.R;
import com.example.openweathermap.ResultWrapper;
import com.example.openweathermap.adapters.ForecastAdapter;
import com.example.openweathermap.api.models.ForecastItem;
import com.example.openweathermap.databinding.ActivityForecastBinding; // Import ViewBinding
import com.example.openweathermap.viewmodels.ForecastViewModel;

import java.util.List;

public class ForecastActivity extends AppCompatActivity {

    private static final String TAG = "ForecastActivity";

    public static final String EXTRA_LATITUDE = "com.example.openweathermap.EXTRA_LATITUDE";
    public static final String EXTRA_LONGITUDE = "com.example.openweathermap.EXTRA_LONGITUDE";

    private ActivityForecastBinding binding;
    private ForecastViewModel forecastViewModel;
    private ForecastAdapter forecastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForecastBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Enable the Up button in ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            Log.w(TAG, "SupportActionBar is null, cannot enable Up button.");
        }


        // Initialize ViewModel
        ForecastViewModelFactory factory = new ForecastViewModelFactory(this);
        forecastViewModel = new ViewModelProvider(this, factory).get(ForecastViewModel.class);


        setupRecyclerView();
        setupObservers();

        // Get coordinates passed from MapsActivity
        double latitude = getIntent().getDoubleExtra(EXTRA_LATITUDE, -999.0);
        double longitude = getIntent().getDoubleExtra(EXTRA_LONGITUDE, -999.0);

        Log.d(TAG, "Received Lat: " + latitude + ", Lon: " + longitude);

        if (latitude != -999.0 && longitude != -999.0) {
            // Fetch forecast data if coordinates are valid
            forecastViewModel.fetchForecast(latitude, longitude);
        } else {
            // Show error if coordinates are missing
            Log.e(TAG, "Error: Location data not received correctly.");
            showError(getString(R.string.error_missing_location));
        }
    }

    // Handle ActionBar Up button press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // android.R.id.home is the ID for the Up button
        if (item.getItemId() == android.R.id.home) {
            finish(); // Simply close this activity and go back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        forecastAdapter = new ForecastAdapter(); // Initialize the adapter
        binding.recyclerViewForecast.setAdapter(forecastAdapter);
        // Set LayoutManager if not set in XML
        binding.recyclerViewForecast.setLayoutManager(new LinearLayoutManager(this));
        // Optional: Add dividers
        // binding.recyclerViewForecast.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    private void setupObservers() {
        forecastViewModel.forecast.observe(this, result -> {
            if (result == null) return;

            switch (result.status) {
                case LOADING:
                    Log.d(TAG, "Forecast loading...");
                    binding.progressBarForecast.setVisibility(View.VISIBLE);
                    binding.recyclerViewForecast.setVisibility(View.GONE);
                    binding.textViewErrorForecast.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    Log.d(TAG, "Forecast success.");
                    binding.progressBarForecast.setVisibility(View.GONE);
                    if (result.data != null && result.data.getList() != null && !result.data.getList().isEmpty()) {
                        binding.textViewErrorForecast.setVisibility(View.GONE);
                        binding.recyclerViewForecast.setVisibility(View.VISIBLE);
                        // Submit data to the adapter
                        forecastAdapter.submitList(result.data.getList());
                        // Update title
                        if (result.data.getCity() != null && result.data.getCity().getName() != null && getSupportActionBar() != null){
                            getSupportActionBar().setTitle(result.data.getCity().getName() + " Forecast");
                        }
                    } else {
                        Log.w(TAG, "Forecast success but list is null or empty.");
                        showError("No forecast data available for this location.");
                    }
                    break;
                case ERROR:
                    Log.e(TAG, "Forecast error: " + result.message, result.error);
                    showError("Error loading forecast: " + result.message);
                    break;
            }
        });
    }

    private void showError(String message) {
        binding.progressBarForecast.setVisibility(View.GONE);
        binding.recyclerViewForecast.setVisibility(View.GONE);
        binding.textViewErrorForecast.setVisibility(View.VISIBLE);
        binding.textViewErrorForecast.setText(message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
