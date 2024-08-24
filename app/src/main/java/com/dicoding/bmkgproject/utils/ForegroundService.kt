package com.dicoding.bmkgproject.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dicoding.bmkgproject.R
import com.dicoding.bmkgproject.data.ApiConfig
import com.dicoding.bmkgproject.data.GempaTerkiniResponse
import com.dicoding.bmkgproject.view.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForegroundService : Service() {


    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private lateinit var job: Job

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)
        job = serviceScope.launch {
            while (true) {
                try {
                    // 1. Make network request
                    val response = makeNetworkRequest()

                    // 2. Process the response
//                    processResponse(response)
                    delay(10000)
                } catch (e: Exception) {
                    // Handle exceptions (e.g., log error)
                    Log.e("ForegroundService", "Error making request: ${e.message}")
                }

                // 3. Wait for a specific interval
            }
        }

//        Log.d(TAG, "Service dijalankan...")
//        serviceScope.launch {
//            val apiService = ApiConfig.getApiService()
//            val response = apiService.getGempaTerkini()
//
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
//                stopForeground(STOP_FOREGROUND_DETACH)
//            }else{
//                stopForeground(true)
//            }
//            stopSelf()
//            Log.d(TAG, "Service dihentikan")
//        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        Log.d(TAG, "onDestroy: Service dihentikan")
    }

    private fun buildNotification(): Notification {

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingFlags: Int = if (Build.VERSION.SDK_INT >= 23) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, pendingFlags)

        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Saat ini foreground service sedang berjalan.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            notificationBuilder.setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_NAME
            notificationBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }

        return notificationBuilder.build()
    }

    // trial
    private fun makeNetworkRequest() {
        // Replace with your actual network request logic
        // Using OkHttp for example:
        var _gempaTerkiniResponse = SingleLiveEvent<GempaTerkiniResponse?>()
        var gempaTerkiniResponse: SingleLiveEvent<GempaTerkiniResponse?>? = _gempaTerkiniResponse

        val client = ApiConfig.getApiService().getGempaTerkini()
        client.enqueue(object : Callback<GempaTerkiniResponse> {
            override fun onResponse(
                call: Call<GempaTerkiniResponse>,
                response: Response<GempaTerkiniResponse>
            ) {
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody != null){
                        _gempaTerkiniResponse.value = responseBody
                        Log.i(TAG, "makeNetworkRequest: $responseBody")
                    }
                }
            }

            override fun onFailure(call: Call<GempaTerkiniResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }

        })

//        Log.d(TAG, "makeNetworkRequest: $gempaTerkiniResponse")

    }

    private fun processResponse(response: String) {
        // Handle the server response (e.g., update UI, show notification)
        // ...
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "dicoding channel"
        internal val TAG = ForegroundService::class.java.simpleName
        private const val INTERVAL_IN_MILLISECONDS = 60000
    }

}