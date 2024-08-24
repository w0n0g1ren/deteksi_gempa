package com.dicoding.bmkgproject.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.bmkgproject.utils.SingleLiveEvent
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository(private val apiService: ApiService) {

    private var _gempaTerkiniResponse = SingleLiveEvent<GempaTerkiniResponse?>()
    var gempaTerkiniResponse: SingleLiveEvent<GempaTerkiniResponse?>? = _gempaTerkiniResponse

    private var _listGempaTerkiniResponse = MutableLiveData<ListGempaResponse>()
    var listGempaTerkiniResponse: LiveData<ListGempaResponse> = _listGempaTerkiniResponse

    fun getGempaTerkini(){
        val client = apiService.getGempaTerkini()
        client.enqueue(object : Callback<GempaTerkiniResponse> {
            override fun onResponse(
                call: Call<GempaTerkiniResponse>,
                response: Response<GempaTerkiniResponse>
            ) {
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody != null){
                        _gempaTerkiniResponse.value = responseBody
                    }
                }
            }

            override fun onFailure(call: Call<GempaTerkiniResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }

        })
    }

    fun listGempaTerkini(){
        val client = apiService.listGempaTerkini()
        client.enqueue(object : Callback<ListGempaResponse> {
            override fun onResponse(call: Call<ListGempaResponse>, response: Response<ListGempaResponse>) {
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody != null){
                        _listGempaTerkiniResponse.value = response.body()
                    }
                }
            }

            override fun onFailure(call: Call<ListGempaResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        private const val TAG = "AppRepository"
        fun getInstance(
            apiService: ApiService,
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService)
            }.also { instance = it }

        fun clearInstance() {
            instance = null
        }
    }
}