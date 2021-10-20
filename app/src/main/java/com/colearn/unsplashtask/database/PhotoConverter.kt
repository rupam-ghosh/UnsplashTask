//package com.colearn.unsplashtask.database
//
//import androidx.room.TypeConverter
//import com.colearn.unsplashtask.Service
//import com.colearn.unsplashtask.pojos.Photo
//
//import com.google.gson.reflect.TypeToken
//import java.lang.reflect.Type
//
//object PhotoConverter {
//    @TypeConverter
//    fun fromString(value: String): Photo {
//        val listType: Type = object : TypeToken<List<Photo>>() {}.type
//        return Service.gson.fromJson(value, listType)
//    }
//
//    @TypeConverter
//    fun fromPhoto(photo: Photo): String {
//        return Service.gson.toJson(photo)
//    }
//}