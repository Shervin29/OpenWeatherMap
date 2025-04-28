package com.example.openweathermap.api;

import com.example.openweathermap.utils.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    private static Retrofit retrofit = null;
    private static OpenWeatherMapApi apiInstance = null;

    private RetrofitClient() {}

    private static Retrofit getClient() {
        if (retrofit == null) {
            // Logging Interceptor
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.OPENWEATHERMAP_BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static OpenWeatherMapApi getApiService() {
        if (apiInstance == null) {
            apiInstance = getClient().create(OpenWeatherMapApi.class);
        }
        return apiInstance;
    }
}
