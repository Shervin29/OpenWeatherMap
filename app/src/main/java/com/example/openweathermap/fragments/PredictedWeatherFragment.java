package com.example.openweathermap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.openweathermap.adapters.ForecastAdapter;
import com.example.openweathermap.databinding.FragmentPredictedWeatherBinding;
import com.example.openweathermap.viewmodels.ForecastViewModel;
import com.example.openweathermap.ResultWrapper; // Add this import!

public class PredictedWeatherFragment extends Fragment {

    private FragmentPredictedWeatherBinding binding;
    private ForecastViewModel viewModel;
    private ForecastAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPredictedWeatherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState); // Important to call

        viewModel = new ViewModelProvider(this).get(ForecastViewModel.class);

        adapter = new ForecastAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        // Fetch forecast for Toronto (can later be dynamic)
        viewModel.fetchForecast(43.65107, -79.347015);

        viewModel.forecast.observe(getViewLifecycleOwner(), result -> {
            if (result != null && result.status == ResultWrapper.Status.SUCCESS && result.data != null) {
                adapter.submitList(result.data.getList());
            }

        });
    }
}
