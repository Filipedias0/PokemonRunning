package com.example.pokedexapp.runningSection.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.pokedexapp.MainActivity
import com.example.pokedexapp.R
import com.example.pokedexapp.other.TrackingUtility
import com.example.pokedexapp.util.constants.Constants.ACTION_PAUSE_SERVICE
import com.example.pokedexapp.util.constants.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.pokedexapp.util.constants.Constants.ACTION_STOP_SERVICE
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.libraries.maps.model.LatLng
import timber.log.Timber
import com.example.pokedexapp.util.constants.Constants.FASTEST_LOCATION_INTERVAL
import com.example.pokedexapp.util.constants.Constants.LOCATION_UPDATE_INTERVAL
import com.example.pokedexapp.util.constants.Constants.TIMER_UPDATE_INTERVAL
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val INTENT_COMMAND = "Command"
const val INTENT_COMMAND_EXIT = "Exit"
const val INTENT_COMMAND_REPLY = "Reply"
const val INTENT_COMMAND_ACHIEVE = "Achieve"

private const val NOTIFICATION_CHANNEL_GENERAL = "Checking"
private const val CODE_FOREGROUND_SERVICE = 1
private const val CODE_REPLY_INTENT = 2
private const val CODE_ACHIEVE_INTENT = 3


typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>
class TrackingService : LifecycleService() {
    private var isFirstRun = true
    private var resuming = false
    private val mutabilityFlag = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }

    private lateinit var  fusedLocationProviderClient: FusedLocationProviderClient
    
    private val timeRunInSeconds = MutableLiveData<Long>()

    companion object{
        val timeRunInMillis = MutableLiveData<Long>()
        var isTracking = MutableLiveData(false)
        val pathPoints = MutableLiveData<Polylines>(mutableListOf())
    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val command = intent?.getStringExtra(INTENT_COMMAND)

        if(command == ACTION_START_OR_RESUME_SERVICE){
            if(isFirstRun) {
                postInitialValues()
                startForegroundService()
                isFirstRun = false
            } else {
                resuming = true
                startTimer()
            }
        }

        if (command == ACTION_STOP_SERVICE) {
            stopService()
            return START_NOT_STICKY
        }

        if (command == ACTION_PAUSE_SERVICE) {
            pauseService()
        }

        if (command == INTENT_COMMAND_REPLY) {
            Toast.makeText(this, "Clicked in Notification", Toast.LENGTH_SHORT).show()
        }

        return START_STICKY
    }

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimeStamp = 0L

    private fun startTimer(){
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while(isTracking.value!!){
                // time difference between now and timeStarted
                lapTime = System.currentTimeMillis() - timeStarted
                // post the new lapTime
                timeRunInMillis.postValue(timeRun + lapTime)
                if(timeRunInMillis.value!! >= lastSecondTimeStamp + 1000L){
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimeStamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean){
        if(isTracking){
            if(TrackingUtility.hasLocationPermissions(this)){
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallBack,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
        }
    }

    private val locationCallBack = object: LocationCallback(){
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if(isTracking.value!!){
                result.locations.let { locations ->
                    for(location in locations){
                        addPathPoint(location)
                        Timber.d("NEW LOCATION: ${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }
    }
    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)

            if(pathPoints.value?.isEmpty() == true){
                pathPoints.postValue(mutableListOf(mutableListOf()))
            }else {
                pathPoints.value?.apply {
                    if (!resuming) {
                        last().add(pos)
                        pathPoints.postValue(this)
                    } else {
                        add(mutableListOf())
                        resuming = false
                        pathPoints.postValue(this)

                    }
                }
            }
        }
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        Timber.d("addEmptyPolyline")
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun stopService() {
        stopForeground(true)
        stopSelf()
    }
    @SuppressLint("LaunchActivityFromNotification")
    private fun startForegroundService() {
        //TODO the color of the background will be the predominant color of the favorite pokemon
        //TODO the background image will be the favorite pokemon
        startTimer()
        isTracking.postValue(true)

        val openActivityIntent = Intent(applicationContext, MainActivity::class.java)
        val openActivityPendingIntent = PendingIntent.getActivity(
            applicationContext, 0, openActivityIntent, mutabilityFlag
        )

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val replyIntent = Intent(this, TrackingService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_REPLY)
        }
        val achieveIntent = Intent(this, TrackingService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_ACHIEVE)
        }
        val replyPendingIntent = PendingIntent.getService(
            this, CODE_REPLY_INTENT, replyIntent, 0
        )
        val achievePendingIntent = PendingIntent.getService(
            this, CODE_ACHIEVE_INTENT, achieveIntent, 0
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                with(
                    NotificationChannel(
                        NOTIFICATION_CHANNEL_GENERAL,
                        "Pokemon running app",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                ) {
                    enableLights(false)
                    setShowBadge(false)
                    enableVibration(false)
                    setSound(null, null)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    manager.createNotificationChannel(this)
                }
            } catch (e: Exception) {
                Log.d("Error", "showNotification: ${e.localizedMessage}")
            }
        }

        with(
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_GENERAL)
        ) {
            val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.poke_ball_pin)
            setTicker(null)
            setContentTitle("Pokemon app")
            setContentText("00:00:00")
            setAutoCancel(false)
            setOngoing(true)
            setWhen(System.currentTimeMillis())
            setSmallIcon(R.drawable.poke_ball_pin)
            setLargeIcon(largeIcon)
            color = Color.parseColor("#f7da64")
            setColorized(true)
            setStyle(androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle())
            priority = Notification.PRIORITY_MAX
            setContentIntent(openActivityPendingIntent)
            addAction(
                0, "REPLY", replyPendingIntent
            )
            addAction(
                0, "ACHIEVE", replyPendingIntent
            )
            startForeground(CODE_FOREGROUND_SERVICE, build())
        }
    }
}
