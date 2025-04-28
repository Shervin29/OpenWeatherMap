package com.example.openweathermap.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.openweathermap.utils.LocationUtils;

import com.bumptech.glide.Glide;
import com.example.openweathermap.R;
import com.example.openweathermap.api.models.CurrentWeatherResponse;
import com.example.openweathermap.api.models.Weather;
import com.example.openweathermap.databinding.ActivityMapsBinding;
import com.example.openweathermap.databinding.InfoWindowWeatherBinding;
import com.example.openweathermap.viewmodels.MapsViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private MapsViewModel mapsViewModel;

    private Location lastKnownLocation = null;
    private Marker currentDisplayedMarker = null;

    private LatLng selectedLatLng = null;

    private final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.getOrDefault(
                        Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = result.getOrDefault(
                        Manifest.permission.ACCESS_COARSE_LOCATION, false);

                if (fineLocationGranted != null && fineLocationGranted) {
                    getLastKnownLocationAndSetupMap();
                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    getLastKnownLocationAndSetupMap();
                } else {
                    Toast.makeText(this, "Location permission denied. Map centered on default.", Toast.LENGTH_LONG).show();
                    setupMapWithDefaultLocation();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapsViewModel = new ViewModelProvider(this).get(MapsViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment_container);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "SupportMapFragment not found!");
            Toast.makeText(this, "Error initializing map.", Toast.LENGTH_LONG).show();
            finish();
        }

        setupObservers();
        setupUIListeners();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            Log.d(TAG, "Map Ready");
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));
            mMap.setOnMapClickListener(latLng -> {
                Log.d(TAG, "Map clicked at: " + LocationUtils.formatLatLng(latLng));
                Log.d(TAG, "Map clicked at: " + latLng);
                selectedLatLng = latLng;
                binding.buttonViewForecast.setEnabled(true);

                binding.progressBarMaps.setVisibility(View.VISIBLE);

                if (currentDisplayedMarker != null) {
                    currentDisplayedMarker.remove();
                }
                currentDisplayedMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Loading weather..."));
                if (currentDisplayedMarker != null) {
                    currentDisplayedMarker.setTag(null);
                    currentDisplayedMarker.showInfoWindow();
                }
                mapsViewModel.fetchWeatherForCoordinates(latLng);
            });

            checkLocationPermission();
        } else {
            Log.e(TAG, "GoogleMap object is null in onMapReady!");
        }
    }

    private void setupObservers() {
        mapsViewModel.currentLocation.observe(this, result -> {
            if (result == null) return;

            switch (result.status) {
                case LOADING:
                    binding.progressBarMaps.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    binding.progressBarMaps.setVisibility(View.GONE);
                    lastKnownLocation = result.data;
                    if (lastKnownLocation != null) {
                        selectedLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        binding.buttonViewForecast.setEnabled(true);
                        moveCameraToLocation(lastKnownLocation, 14f);
                        enableLocationFeatures();
                    } else {
                        if (mMap != null) setupMapWithDefaultLocation();
                    }
                    break;
                case ERROR:
                    binding.progressBarMaps.setVisibility(View.GONE);
                    binding.buttonViewForecast.setEnabled(false);
                    Toast.makeText(this, "Error getting your location: " + result.message, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error getting location: " + result.message, result.error);
                    if (mMap != null) setupMapWithDefaultLocation();
                    break;
            }
        });

        mapsViewModel.tappedLocationWeather.observe(this, result -> {
            if (result == null) return;
            binding.progressBarMaps.setVisibility(View.GONE);

            switch (result.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    if (currentDisplayedMarker != null && result.data != null) {
                        currentDisplayedMarker.setTag(result.data);
                        currentDisplayedMarker.setTitle(result.data.getName() != null ? result.data.getName() : "Weather Info");
                        currentDisplayedMarker.showInfoWindow();
                    }
                    break;
                case ERROR:
                    if (currentDisplayedMarker != null) {
                        currentDisplayedMarker.setTag(result.message);
                        currentDisplayedMarker.setTitle("Error");
                        currentDisplayedMarker.showInfoWindow();
                    }
                    break;
            }
        });
    }

    private void setupUIListeners() {
        binding.buttonViewForecast.setEnabled(false);
        binding.buttonViewForecast.setOnClickListener(v -> {
            if (selectedLatLng != null) {
                Intent intent = new Intent(MapsActivity.this, ForecastActivity.class);
                intent.putExtra(ForecastActivity.EXTRA_LATITUDE, selectedLatLng.latitude);
                intent.putExtra(ForecastActivity.EXTRA_LONGITUDE, selectedLatLng.longitude);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please tap on the map to select a location!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkLocationPermission() {
        if (hasLocationPermission()) {
            getLastKnownLocationAndSetupMap();
        } else {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    private void getLastKnownLocationAndSetupMap() {
        binding.progressBarMaps.setVisibility(View.VISIBLE);
        mapsViewModel.fetchCurrentLocation();
    }

    @SuppressLint("MissingPermission")
    private void enableLocationFeatures() {
        if (mMap != null && hasLocationPermission()) {
            try {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } catch (SecurityException e) {
                Log.e(TAG, "SecurityException enabling location features.", e);
            }
        }
    }

    private void moveCameraToLocation(@NonNull Location location, float zoomLevel) {
        if (mMap != null) {
            LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, zoomLevel));
        }
    }

    private void setupMapWithDefaultLocation() {
        if (mMap != null) {
            LatLng defaultLocation = new LatLng(43.6532, -79.3832);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f));
        }
    }

    static class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final InfoWindowWeatherBinding binding;
        private final LayoutInflater inflater;

        CustomInfoWindowAdapter(MapsActivity activity) {
            this.inflater = LayoutInflater.from(activity);
            this.binding = InfoWindowWeatherBinding.inflate(inflater, null, false);
        }

        @Nullable
        @Override
        public View getInfoWindow(@NonNull Marker marker) {
            return null;
        }

        @Nullable
        @Override
        public View getInfoContents(@NonNull Marker marker) {
            Object tag = marker.getTag();

            if (tag instanceof CurrentWeatherResponse) {
                CurrentWeatherResponse data = (CurrentWeatherResponse) tag;
                binding.infoTitle.setText(data.getName() != null ? data.getName() : marker.getTitle());

                String tempText = "N/A";
                if (data.getMain() != null && data.getMain().getTemp() != null) {
                    int temp = (int) Math.round(data.getMain().getTemp());
                    tempText = binding.getRoot().getContext().getString(R.string.info_temp_format_value, temp);
                }
                binding.infoTemperature.setText(tempText);

                String descText = "N/A";
                Weather weather = null;
                if (data.getWeather() != null && !data.getWeather().isEmpty()) {
                    weather = data.getWeather().get(0);
                }
                if (weather != null && weather.getDescription() != null) {
                    String rawDesc = weather.getDescription();
                    descText = rawDesc.substring(0, 1).toUpperCase(Locale.getDefault()) + rawDesc.substring(1);
                }
                binding.infoDescription.setText(binding.getRoot().getContext().getString(R.string.info_desc_format, descText));

                String iconCode = (weather != null) ? weather.getIcon() : null;
                if (iconCode != null) {
                    binding.infoIcon.setVisibility(View.VISIBLE);
                    String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                    Glide.with(binding.getRoot().getContext())
                            .load(iconUrl)
                            .error(R.drawable.ic_launcher_foreground)
                            .into(binding.infoIcon);
                } else {
                    binding.infoIcon.setVisibility(View.GONE);
                }

            } else if (tag instanceof String) {
                binding.infoTitle.setText("Error");
                binding.infoTemperature.setText((String) tag);
                binding.infoDescription.setText("");
                binding.infoIcon.setVisibility(View.GONE);
            } else {
                binding.infoTitle.setText(marker.getTitle() != null ? marker.getTitle() : "Loading...");
                binding.infoTemperature.setText("...");
                binding.infoDescription.setText("...");
                binding.infoIcon.setVisibility(View.GONE);
            }

            return binding.getRoot();
        }
    }
}
