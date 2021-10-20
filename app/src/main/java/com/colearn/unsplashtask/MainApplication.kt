package com.colearn.unsplashtask

import android.app.Application
import androidx.room.Room
//import com.colearn.unsplashtask.database.AppDatabase
import com.colearn.unsplashtask.network.TokenInterceptor
import com.colearn.unsplashtask.network.UnsplashService
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupRetrofit()
    }

    private fun setupRetrofit() {
        val client = OkHttpClient.Builder().addInterceptor(TokenInterceptor()).build()
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.unsplash.com/")
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .client(client)
                .build()
        Service.unsplashService = retrofit.create(UnsplashService::class.java)
//        Service.db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java, "database-name"
//        ).build()
    }
}