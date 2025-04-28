package com.example.openweathermap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class ResultWrapper<T> {

    @NonNull
    public final Status status;
    @Nullable
    public final T data;
    @Nullable
    public final String message;
    @Nullable
    public final Throwable error;

    private ResultWrapper(@NonNull Status status, @Nullable T data, @Nullable String message, @Nullable Throwable error) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.error = error;
    }

    public static <T> ResultWrapper<T> loading() {
        return new ResultWrapper<>(Status.LOADING, null, null, null);
    }

    public static <T> ResultWrapper<T> success(@NonNull T data) {
        return new ResultWrapper<>(Status.SUCCESS, data, null, null);
    }

    public static <T> ResultWrapper<T> error(String msg, @Nullable Throwable error) {
        return new ResultWrapper<>(Status.ERROR, null, msg, error);
    }

    public enum Status {
        LOADING,
        SUCCESS,
        ERROR
    }
}
