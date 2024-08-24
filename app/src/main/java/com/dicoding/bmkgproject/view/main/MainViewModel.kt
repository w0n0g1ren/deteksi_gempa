package com.dicoding.bmkgproject.view.main

import androidx.lifecycle.ViewModel
import com.dicoding.bmkgproject.data.GempaTerkiniResponse
import com.dicoding.bmkgproject.data.Repository
import com.dicoding.bmkgproject.utils.SingleLiveEvent

class MainViewModel(private val repository: Repository): ViewModel() {

    var getGempaTerkiniResponse: SingleLiveEvent<GempaTerkiniResponse?>? = repository.gempaTerkiniResponse

    fun getGempaTerkini(){
        return repository.getGempaTerkini()
    }
}