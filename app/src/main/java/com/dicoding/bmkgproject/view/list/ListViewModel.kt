package com.dicoding.bmkgproject.view.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.bmkgproject.data.Gempa
import com.dicoding.bmkgproject.data.ListGempaResponse
import com.dicoding.bmkgproject.data.Repository

class ListViewModel(private val repository: Repository): ViewModel() {
    var listGempaTerkiniResponse: LiveData<ListGempaResponse> = repository.listGempaTerkiniResponse

    fun listGempaTerkini(){
        repository.listGempaTerkini()
    }
}