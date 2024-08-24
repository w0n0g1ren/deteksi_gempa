package com.dicoding.bmkgproject.view.main


import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.dicoding.bmkgproject.databinding.ActivityMainBinding
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.dicoding.bmkgproject.utils.MyWorkManager
import com.dicoding.bmkgproject.R
import com.dicoding.bmkgproject.view.ViewModelFactory
import com.dicoding.bmkgproject.view.list.ListActivity
import org.osmdroid.config.Configuration.*
import org.osmdroid.views.overlay.Marker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance()
    }

    private lateinit var workManager: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean? ->
        if (!isGranted!!)
            Toast.makeText(this,
                "Unable to display Foreground service notification due to permission decline",
                Toast.LENGTH_LONG)
    }

    private lateinit var binding: ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnList.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
        }
        viewModel.getGempaTerkini()
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        viewModel.getGempaTerkiniResponse?.observe(this){

            binding.tanggal.text = it?.infogempa?.gempa?.tanggal
            binding.jam.text = it?.infogempa?.gempa?.jam
            binding.kordinat.text = it?.infogempa?.gempa?.coordinates
            binding.magnitudo.text = it?.infogempa?.gempa?.magnitude
            binding.kedalaman.text = it?.infogempa?.gempa?.kedalaman
            binding.pusatGempa.text = it?.infogempa?.gempa?.wilayah

            var cordinate = converterString(it?.infogempa?.gempa?.coordinates.toString())

            var latitude = cordinate.first
            var longitude = cordinate.second

            println(latitude)
            println(longitude)

            Log.e(TAG,"$latitude, $longitude")
            binding.map.setTileSource(TileSourceFactory.MAPNIK)
            val mapController = binding.map.controller
            mapController.setZoom(10.5)
            val startPoint = GeoPoint(latitude, longitude);
            mapController.setCenter(startPoint)
            var marker = Marker(binding.map)
            marker.position = startPoint
            marker.title = "titik gempa"
            marker.icon = ContextCompat.getDrawable(this, R.drawable.location)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            binding.map.overlays.add(marker)
            binding.map.invalidate()
        }

        workManager = WorkManager.getInstance(this)
        startPeriodicTask()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
//                PackageManager.PERMISSION_GRANTED)
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//        }
//        val foregroundServiceIntent = Intent(this, ForegroundService()::class.java)
//
//        startForegroundService(foregroundServiceIntent)


    }

    private fun startPeriodicTask() {
        val comstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorkManager::class.java, 15, TimeUnit.MINUTES)
            .setConstraints(comstraints)
            .build()
        workManager.enqueue(periodicWorkRequest)
        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id)
            .observe(this@MainActivity) { workInfo ->
                val status = workInfo.state.name
                Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
            }
    }

//    private fun startOneTimeTask() {
//        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
//        periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorkManager::class.java, 15, TimeUnit.MINUTES)
//            .setConstraints(constraints)
//            .build()
//        workManager.enqueue(periodicWorkRequest)
//        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id)
//            .observe(this@MainActivity) { workInfo ->
//                val status = workInfo.state.name
//                 {
//
//                }
//            }
//    }

    private fun converterString(string: String): Pair<Double, Double> {
        val numberPart = string.substringBefore(",")
        val numberPart2 = string.substringAfter(",")
        val realNumber: Double = numberPart.toDouble()
        val realNumber2: Double = numberPart2.toDouble()
        return Pair(realNumber, realNumber2)
    }

    companion object {
        private const val TAG = "AppRepository"
    }

}