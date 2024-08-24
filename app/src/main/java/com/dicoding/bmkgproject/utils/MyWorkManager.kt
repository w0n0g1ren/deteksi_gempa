package com.dicoding.bmkgproject.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.bmkgproject.R
import com.dicoding.bmkgproject.view.main.MainActivity
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.SyncHttpClient
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class MyWorkManager(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private var resultStatus: Result? = null
    private var context = context
    override fun doWork(): Result {
        return getCurrentGempa(context)
    }

    private fun getCurrentGempa(context: Context): Result {
        Log.d(TAG, "Mulai.....")
        Looper.prepare()
        val client = SyncHttpClient()
        val url = "https://data.bmkg.go.id/DataMKG/TEWS/autogempa.json"
        Log.d(TAG, "$url")
        client.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {

                val result = String(responseBody!!)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val getDetail = responseObject.getJSONObject("Infogempa").getJSONObject("gempa")
                    val getJam = getDetail.getString("Jam")
                    val getWilayah = getDetail.getString("Wilayah")
                    Log.d(TAG,"onSuccess: Berhasil $getJam $getWilayah")
                    getNotification(context,getJam, getWilayah)
                    resultStatus = Result.success()
                }catch (e : Exception){
                    Log.d(TAG, "onSuccess: Gagal.....")
                    resultStatus = Result.failure()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Log.d(TAG, "onFailure: Gagal.....")
                // ketika proses gagal, maka jobFinished diset dengan parameter true. Yang artinya job perlu di reschedule
                resultStatus = Result.failure()
            }
        })
        return resultStatus as Result


    }

    private fun getNotification(appContext: Context,jam: String, lokasi: String){
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(appContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(appContext,
            0, intent, PendingIntent.FLAG_MUTABLE)
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.notif)
            .setContentTitle(jam)
            .setContentText(lokasi)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notification.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    companion object {
        private val TAG = MyWorkManager::class.java.simpleName
        const val APP_ID = "YOUR_KEY_HERE"
        const val EXTRA_CITY = "city"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "dicoding channel"
    }
}