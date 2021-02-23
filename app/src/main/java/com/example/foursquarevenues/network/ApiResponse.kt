package com.example.foursquarevenues.network


sealed class ApiResponse<out T>{
    data class Success<out T>(val data: T) : ApiResponse<T>()
    object Failure: ApiResponse<Nothing>()

    //TODO: Maybe add the reason to the failure later on
    //data class Failure(val reason: String) : ApiResponse<Nothing>()
}