package com.karunadakote.data.api

import com.karunadakote.data.model.Fort
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("forts") // Make sure this endpoint matches your backend
    suspend fun getForts(): Response<List<Fort>>
}