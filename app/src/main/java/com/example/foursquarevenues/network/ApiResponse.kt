package com.example.foursquarevenues.network


sealed class ApiResponse<out T>{
    data class Success<out T>(val data: T) : ApiResponse<T>()

    // In actual app I would return a message and an exception within the failure class
    object Failure: ApiResponse<Nothing>()
}