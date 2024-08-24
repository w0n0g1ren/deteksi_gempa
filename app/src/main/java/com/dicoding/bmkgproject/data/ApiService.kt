package com.dicoding.bmkgproject.data

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("autogempa.json")
    fun getGempaTerkini(): Call<GempaTerkiniResponse>

    @GET("gempaterkini.json")
    fun listGempaTerkini(): Call<ListGempaResponse>

}