package com.example.newsapplication.util

sealed class Resource<T> (val data: T? = null, val error: Throwable? = null){
    class Loading<T>( data: T? = null, error: Throwable? = null): Resource<T>(data)
    class Success<T>(data: T, error: Throwable? = null): Resource<T>(data)
    class Error<T>(data: T? = null, error: Throwable): Resource<T>(data, error)
}
