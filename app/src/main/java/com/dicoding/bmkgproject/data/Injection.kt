package com.dicoding.bmkgproject.data

import android.content.Context
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(): Repository = runBlocking  {
        val apiService = ApiConfig.getApiService()
        Repository.getInstance(apiService)
    }
}