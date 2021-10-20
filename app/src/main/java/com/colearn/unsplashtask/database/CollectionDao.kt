//package com.colearn.unsplashtask.database
//
//import androidx.lifecycle.LiveData
//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.Query
//
//@Dao
//interface CollectionDao {
//    @Query("SELECT * FROM collection_photo")
//    fun getAllPhotos(): LiveData<List<CollectionPhoto>>
//
//    @Insert
//    fun insertAll(collectionPhoto: List<CollectionPhoto>)
//
//    @Delete
//    fun delete(vararg collectionPhoto: CollectionPhoto)
//}