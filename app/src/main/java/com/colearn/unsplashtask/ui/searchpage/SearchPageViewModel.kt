package com.colearn.unsplashtask.ui.searchpage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colearn.unsplashtask.Service
import com.colearn.unsplashtask.constants.Constants.PAGE_SIZE
import com.colearn.unsplashtask.pojos.ApiStatus
import com.colearn.unsplashtask.pojos.SearchApiResult
import com.colearn.unsplashtask.pojos.SearchResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchPageViewModel : ViewModel() {
    val searchApiLiveData: MutableLiveData<SearchApiResult> = MutableLiveData()
    var lastSuccessfulPage: Int = 0
    var currentPageToBeFetched: Int = -1

    private fun search(query: String, orderBy: String, queryMap: MutableMap<String, String>) {
        val pageToBeFetched: Int = lastSuccessfulPage + 1
        if (currentPageToBeFetched == pageToBeFetched) {
            // this logic will prevent api calls for the same page
            return
        }
        currentPageToBeFetched = pageToBeFetched
        searchApiLiveData.value = SearchApiResult(ApiStatus.LOADING, searchApiLiveData.value?.data)
        Service.unsplashService.search(query, pageToBeFetched, PAGE_SIZE, orderBy, queryMap).enqueue(object : Callback<SearchResult> {
            override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {
                val photoList = response.body()
                if (photoList == null || photoList.results?.size == 0) {
                    // code will reach here when the user has reached the last page
                    searchApiLiveData.value = SearchApiResult(ApiStatus.SUCCESS, searchApiLiveData.value?.data)
                    return
                }
                if (searchApiLiveData.value?.data?.results == null) {
                    // this code will execute for first page response of the api
                    photoList.let { searchApiLiveData.value = SearchApiResult(ApiStatus.SUCCESS, photoList) }
                } else {
                    // this code will execute for subsequent page responses of the api
                    val lastData = searchApiLiveData.value?.data
                    photoList.let {
                        it.results?.let { it1 -> lastData?.results?.addAll(it1) }
                        // no need to change total results and total pages as they will remain same
                    }
                    searchApiLiveData.value = SearchApiResult(ApiStatus.SUCCESS, lastData)
                }
                lastSuccessfulPage = pageToBeFetched
            }

            override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                searchApiLiveData.value = SearchApiResult(ApiStatus.FAILURE, searchApiLiveData.value?.data)
                // setting currentPageToBeFetched to -1 makes sure that the failed page can be retried by the user
                currentPageToBeFetched = -1
            }
        })
    }

    fun clear() {
        // calling clear function will make sure that the api call starts from the first page
        lastSuccessfulPage = 0
        currentPageToBeFetched = -1
        searchApiLiveData.value = SearchApiResult(ApiStatus.LOADING, null)
    }

    fun initiateSearch(query: String) {
        search(query, "relevant", mutableMapOf())
    }

    fun advancedSearch(query: String, orderBy: String, queryMap: MutableMap<String, String>) {
        search(query, orderBy, queryMap)
    }
}