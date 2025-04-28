package com.example.openweathermap.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.openweathermap.R;
import com.example.openweathermap.api.models.ForecastItem;

import com.example.openweathermap.databinding.ItemForecastBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ForecastAdapter extends ListAdapter<ForecastItem, ForecastAdapter.ForecastViewHolder> {

    private static final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, MMM d, h:mm a", Locale.getDefault());

    public ForecastAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemForecastBinding binding = ItemForecastBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ForecastViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        ForecastItem item = getItem(position);
        if (item != null) {
            holder.bind(item);
        }
    }

    static class ForecastViewHolder extends RecyclerView.ViewHolder {
        private final ItemForecastBinding binding;

        ForecastViewHolder(@NonNull ItemForecastBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ForecastItem item) {
            Context context = itemView.getContext();

            // Format datetime
            String displayDateTime = "N/A";
            try {
                Date date = inputFormat.parse(item.getDtTxt());
                if (date != null) {
                    displayDateTime = outputFormat.format(date);
                }
            } catch (ParseException | NullPointerException e) {
                displayDateTime = item.getDtTxt(); // fallback
            }
            binding.itemForecastDatetime.setText(displayDateTime);

            // Format temperature
            String tempText = "N/A";
            if (item.getMain() != null && item.getMain().getTemp() != null) {
                int temp = (int) Math.round(item.getMain().getTemp());
                tempText = context.getString(R.string.info_temp_format_value, temp);
            }
            binding.itemForecastTemp.setText(tempText);

            // Weather description
            String descText = "N/A";
            if (item.getWeather() != null && !item.getWeather().isEmpty() && item.getWeather().get(0) != null) {
                String rawDesc = item.getWeather().get(0).getDescription();
                if (rawDesc != null && !rawDesc.isEmpty()) {
                    descText = rawDesc.substring(0, 1).toUpperCase(Locale.getDefault()) + rawDesc.substring(1);
                }
            }
            binding.itemForecastDescription.setText(descText);

            // Load weather icon
            String iconCode = (item.getWeather() != null && !item.getWeather().isEmpty()) ? item.getWeather().get(0).getIcon() : null;
            if (iconCode != null) {
                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                Glide.with(context)
                        .load(iconUrl)
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(binding.itemForecastIcon);
            } else {
                binding.itemForecastIcon.setImageResource(R.mipmap.ic_launcher);
            }
        }
    }

    private static final DiffUtil.ItemCallback<ForecastItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ForecastItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull ForecastItem oldItem, @NonNull ForecastItem newItem) {
                    return Objects.equals(oldItem.getDt(), newItem.getDt());
                }

                @Override
                public boolean areContentsTheSame(@NonNull ForecastItem oldItem, @NonNull ForecastItem newItem) {
                    return Objects.equals(oldItem.getDtTxt(), newItem.getDtTxt()) &&
                            Objects.equals(oldItem.getMain(), newItem.getMain()) &&
                            Objects.equals(oldItem.getWeather(), newItem.getWeather());
                }
            };
}
