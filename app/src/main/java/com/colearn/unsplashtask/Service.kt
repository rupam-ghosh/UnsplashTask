package com.colearn.unsplashtask

import androidx.room.RoomDatabase
//import com.colearn.unsplashtask.database.AppDatabase
import com.colearn.unsplashtask.network.UnsplashService
import com.google.gson.Gson

object Service {
    lateinit var unsplashService: UnsplashService
//    lateinit var db: AppDatabase
    lateinit var gson: Gson
}