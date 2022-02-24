package com.example.pokedexapp.runningSection.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.request.ImageRequest
import com.example.pokedexapp.R
import com.example.pokedexapp.other.TrackingUtility
import com.example.pokedexapp.repository.PokemonRepository
import com.example.pokedexapp.runningSection.startRunScreen.StartRunViewModel
import com.example.pokedexapp.util.constants.Constants.ACTION_PAUSE_SERVICE
import com.example.pokedexapp.util.constants.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.pokedexapp.util.constants.Constants.ACTION_STOP_SERVICE
import com.example.pokedexapp.util.constants.Constants.FASTEST_LOCATION_INTERVAL
import com.example.pokedexapp.util.constants.Constants.LOCATION_UPDATE_INTERVAL
import com.example.pokedexapp.util.constants.Constants.NOTIFICATION_CHANNEL_GENERAL
import com.example.pokedexapp.util.constants.Constants.NOTIFICATION_ID
import com.example.pokedexapp.util.constants.Constants.TIMER_UPDATE_INTERVAL
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.libraries.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

const val INTENT_COMMAND = "Command"
const val INTENT_COMMAND_REPLY = "Reply"

private const val CODE_FOREGROUND_SERVICE = 1

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class TrackingService : LifecycleService() {
    private var isFirstRun = true
    private var resuming = false
    private var serviceKilled = false
    private val notificationImage = MutableLiveData<Bitmap>()
    private val dominantColor = MutableLiveData<Int>()

    @Inject
    lateinit var  fusedLocationProviderClient: FusedLocationProviderClient

    private val timeRunInSeconds = MutableLiveData<Long>()

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    private lateinit var curNotificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var repository: PokemonRepository

    @ExperimentalCoilApi
    private fun loadNotificationImage(
        context: Context,
        pokemonNumber: Int
    ) {
            // generated random from 0 to last indices included
            val url =
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokemonNumber}.png"

            val imageLoader = ImageLoader.Builder(context)
                .availableMemoryPercentage(0.25)
                .crossfade(true)
                .build()

            val request = ImageRequest.Builder(context)
                .data(url)
                .target {
                    val bmp = (it as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
                    notificationImage.postValue(bmp)
                }
                .build()

            imageLoader.enqueue(request)
    }

    companion object{
        val timeRunInMillis = MutableLiveData<Long>()
        var isTracking = MutableLiveData(false)
        val pathPoints = MutableLiveData<Polylines>(mutableListOf())
        val endRunAndSaveIntoDb = MutableLiveData(false)
    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    private fun killService(){
        serviceKilled = true
        isFirstRun = true
        pauseService()
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }

    private fun calcDominantColor(
        bmp: Bitmap,
        onFinish: (Int) -> Unit
    ) {
        Palette.from(bmp).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(colorValue)
            }
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    private fun subscribeToObservers(context: Context){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        isTracking.observe(this) {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        }

        notificationImage.observe(this) {
            calcDominantColor(it) { intColor ->
                dominantColor.postValue(intColor)
            }

            curNotificationBuilder = baseNotificationBuilder
                .setLargeIcon(it)
            notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder.build())
        }

        dominantColor.observe(this) {
            curNotificationBuilder = baseNotificationBuilder
                .setColor(it)
            notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder.build())
        }

        StartRunViewModel.pokemonNumber.observe(this){
            loadNotificationImage(context, it)
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    override fun onCreate() {
        super.onCreate()
        val context = this.applicationContext
        curNotificationBuilder = baseNotificationBuilder
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

       subscribeToObservers(context)
    }

    @OptIn(ExperimentalCoilApi::class)
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
            killService()
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

    private fun updateNotificationTrackingState(isTracking: Boolean){
        val notificationActionText = if(isTracking) "Pause" else "Resume"
        val pendingIntent = if(isTracking){
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                putExtra(INTENT_COMMAND, ACTION_PAUSE_SERVICE)
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        }
        else{
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                putExtra(INTENT_COMMAND, ACTION_START_OR_RESUME_SERVICE)
            }
            PendingIntent.getService(this,2,resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }
        if(!serviceKilled){
            curNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.ic_pause_black_24dp, notificationActionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder.build())
        }
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
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    @SuppressLint("LaunchActivityFromNotification")
    private fun startForegroundService() {
        //TODO the color of the background will be the predominant color of the favorite pokemon
        //TODO the background image will be the favorite pokemon
        startTimer()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
                    notificationManager.createNotificationChannel(this)
                }
            } catch (e: Exception) {
                Log.d("Error", "showNotification: ${e.localizedMessage}")
            }
        }

            startForeground(CODE_FOREGROUND_SERVICE, baseNotificationBuilder.build())

            timeRunInSeconds.observe(this) {
                if (!serviceKilled) {
                    val notification = curNotificationBuilder
                        .setContentText(TrackingUtility.getFormattedStopWatchTime(it * 1000L))
                    notificationManager.notify(NOTIFICATION_ID, notification.build())
                }
            }
    }
    }

