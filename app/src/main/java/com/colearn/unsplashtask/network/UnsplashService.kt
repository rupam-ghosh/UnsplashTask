package com.colearn.unsplashtask.network

import com.colearn.unsplashtask.pojos.Photo
import com.colearn.unsplashtask.pojos.SearchResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface UnsplashService {
    @GET("collections/{id}/photos")
    fun getPhotosForCollection(@Path("id") collectionId: String, @Query("page") page: Int, @Query("per_page") perPage: Int): Call<ArrayList<Photo>>

    @GET("search/photos")
    fun search(@Query("query") query: String, @Query("page") page: Int, @Query("per_page") perPage: Int, @Query("order_by") orderBy: String, @QueryMap queryMap: MutableMap<String, String>): Call<SearchResult>
}