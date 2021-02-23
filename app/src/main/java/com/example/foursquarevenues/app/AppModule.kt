package com.example.foursquarevenues.app

import Constants
import android.app.Application
import com.example.foursquarevenues.network.FoursquareApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
class AppModule(val application: Application) {

    @Provides
    @AppScope
    fun retrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun application() = application

    @Provides
    @AppScope
    fun foursquareApi(retrofit: Retrofit) = retrofit.create(FoursquareApi::class.java)

}