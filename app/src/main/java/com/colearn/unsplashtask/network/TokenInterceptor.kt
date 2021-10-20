package com.colearn.unsplashtask.network

import com.colearn.unsplashtask.constants.Constants.ACCESS_KEY
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
                .addHeader("Authorization", "Client-ID "+ACCESS_KEY)
                .build()
        return chain.proceed(request)
    }
}