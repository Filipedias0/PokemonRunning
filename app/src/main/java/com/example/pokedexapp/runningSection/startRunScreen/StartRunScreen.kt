package com.example.pokedexapp.runningSection.startRunScreen

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.pokedexapp.R
import com.example.pokedexapp.db.Run
import com.example.pokedexapp.other.TrackingUtility
import com.example.pokedexapp.runningSection.service.TrackingService
import com.example.pokedexapp.runningSection.service.sendCommandToService
import com.example.pokedexapp.util.PermissionsHandler
import com.example.pokedexapp.util.constants.Constants.ACTION_PAUSE_SERVICE
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.PolylineOptions
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.pokedexapp.util.constants.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.pokedexapp.util.constants.Constants.ACTION_STOP_SERVICE
import com.example.pokedexapp.util.constants.Constants.MAP_ZOOM
import com.example.pokedexapp.util.constants.Constants.POLYLINE_COLOR
import com.example.pokedexapp.util.constants.Constants.POLYLINE_WIDTH
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.LatLngBounds
import timber.log.Timber
import java.util.*
import kotlin.math.round

@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalPermissionsApi
@Composable
fun StartRunScreen(
    navController: NavController,
    viewModel: StartRunViewModel = hiltViewModel()
) {
    val showPermissionsDialog = remember { mutableStateOf(false) }
    val showFinishRunDialog = remember { mutableStateOf(false) }
    var textTimer = remember { mutableStateOf("00:00:00") }
    val lifecycleOwner = LocalLifecycleOwner.current
    var curTimeInMillis = 0L
    val context = LocalContext.current

    fun cancelRun(){
        sendCommandToService(context = context, command = ACTION_STOP_SERVICE)
        textTimer.value = "00:00:00:00"
        navController.navigate(
            "runs_screen"
        )
    }

    fun subscribeToObservers() {
        TrackingService.timeRunInMillis.observe(lifecycleOwner, {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis, true)
            textTimer.value = formattedTime
        })

        StartRunViewModel.saveRun.observe(lifecycleOwner){
            viewModel.insertRun(it)
            textTimer.value = "00:00:00:00"

            navController.navigate(
                "runs_screen"
            )
        }
    }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        PermissionsHandler(showAlert = showPermissionsDialog, false)
        subscribeToObservers()

        if(showFinishRunDialog.value){
            FinishRunDialog(
                showAlert = showFinishRunDialog,
                context = LocalContext.current,
                navController = navController,
                textTimer = textTimer
            )
        }

        RunningWrapper(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 48.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colors.secondary),
            navController = navController,
            showFinishRunDialog =   showFinishRunDialog,
            textTimer = textTimer,
            curTimeInMillis = curTimeInMillis,
            viewModel = viewModel,
            cancelRun = { cancelRun() }
        )
    }

}

@Composable
fun RunningWrapper(
    modifier: Modifier = Modifier,
    navController: NavController,
    showFinishRunDialog: MutableState<Boolean>,
    textTimer: MutableState<String>,
    curTimeInMillis: Long,
    viewModel: StartRunViewModel,
    cancelRun: () -> Unit,
    ) {
    val isTracking by TrackingService.isTracking.observeAsState(false)
    lateinit var context: Context
    val lifecycleOwner = LocalLifecycleOwner.current
    val pathPoints by TrackingService.pathPoints.observeAsState()
    val mapView = rememberMapViewWithLifeCycle()
    val weight = 80f

    fun toggleRun(){
        if(isTracking){
            sendCommandToService(ACTION_PAUSE_SERVICE, context)
        }else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE, context)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
    ) {
        MapHandler()
        context = LocalContext.current

        var gifLoader = ImageLoader.Builder(LocalContext.current)
            .componentRegistry {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder(LocalContext.current))
                } else {
                    add(GifDecoder())
                }
            }
            .build()

        val painter = rememberImagePainter(R.drawable.pikachu_running, gifLoader)
        Image(
            painter = painter,
            contentDescription = "Pikachu running",
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.CenterHorizontally)
        )

        Text(
            text = textTimer.value,
            fontSize = 48.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0, 103, 180),
        )

        Button(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .offset(
                    y = 30.dp
                ),
            onClick = {
                toggleRun()
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(Color(255, 203, 8))
        ) {
            Text(
                text = if(!isTracking) "Start" else "Stop",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0, 103, 180)
            )
        }

        if (isTracking){
            Button(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .offset(
                        y = 40.dp
                    ),
                onClick = {
                    showFinishRunDialog.value = true
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color(255, 203, 8))
            ) {
                Text(
                    text = "Finish run",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0, 103, 180)
                )
            }
        }
    }
}

@Composable
fun MapHandler() {
    val mapView = rememberMapViewWithLifeCycle()
    var map: GoogleMap? = null
    val pathPoints by TrackingService.pathPoints.observeAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val weight = 80f
    val context = LocalContext.current
    var curTimeInMillis = 0L


    fun zoomToSeeWholeTrack(){
        val bounds = LatLngBounds.Builder()
        for(polyline in pathPoints!!){
            for(pos in polyline) {
                bounds.include(pos)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05F).toInt()
            )
        )
    }

    fun cancelRun(){
        sendCommandToService(context = context, command = ACTION_STOP_SERVICE)
    }

    fun endRunAndSaveToDb(){
        map?.snapshot { bmp ->
            var distanceInMeters = 0
            for(polyline in pathPoints!!){
                distanceInMeters += TrackingUtility.calculatePolylineLenght(polyline).toInt()
            }
            val avgSpeed = round(
                (distanceInMeters / 1000f) / ( curTimeInMillis/ 1000f / 60 / 60)*10
            ) / 10f
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters /1000f) * weight) .toInt()
            val run = Run( bmp, dateTimeStamp, avgSpeed, distanceInMeters, curTimeInMillis, caloriesBurned)

            StartRunViewModel.saveRun.postValue(run)
            Toast.makeText(
                context, "Run saved succesfully!", LENGTH_SHORT
            ).show()
            cancelRun()
        }
    }

    fun addAllPolylines() {
        Timber.d("addAllPolylines")
        for (polyline in pathPoints!!) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    fun moveCameraToUser() {
        if (pathPoints?.isNotEmpty() == true && pathPoints?.last()?.isNotEmpty() == true) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints!!.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    fun addLatestPolyline() {
        Timber.d("addLatestPolyline")
        //TODO the color of the polyline will be the predominant color of the favorite pokemon
        //TODO make the favorite pokemon run in the map ex
        // val markerOptions = MarkerOptions()
        //                    .title("PickupPosition")
        //                    .position(pickUp)
        //                map!!.addMarker(markerOptions)
        if (pathPoints!!.isNotEmpty() && pathPoints!!.last().size > 1) {
            val preLastLatLng = pathPoints!!.last()[pathPoints!!.last().size - 2]
            val lastLatLng = pathPoints!!.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    fun subscribeToObservers(){
        TrackingService.pathPoints.observe(lifecycleOwner, {
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.endRunAndSaveIntoDb.observe(lifecycleOwner){
            if(it) {
                zoomToSeeWholeTrack()
                endRunAndSaveToDb()
            }
        }

        TrackingService.timeRunInMillis.observe(lifecycleOwner, Observer {
            curTimeInMillis = it
        })
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize(0.5f)
            .background(Color.White)
    ) {
        AndroidView(
            { mapView }
        ) { mapView ->
            CoroutineScope(Dispatchers.Main).launch {
                map = mapView.awaitMap()
                addAllPolylines()
                subscribeToObservers()
            }
        }
    }
}

@Composable
fun rememberMapViewWithLifeCycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = com.google.maps.android.ktx.R.id.map_frame
        }
    }
    val lifeCycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifeCycleObserver)
        onDispose {
            lifecycle.removeObserver(lifeCycleObserver)
        }
    }
    return mapView
}

@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> throw IllegalStateException()
            }
        }
    }

@Composable
fun FinishRunDialog(
    showAlert: MutableState<Boolean>,
    context: Context,
    navController: NavController,
    textTimer: MutableState<String>
) {
    val title = "Finish run"

    fun cancelRun(){
        sendCommandToService(context = context, command = ACTION_STOP_SERVICE)
        textTimer.value = "00:00:00:00"
        navController.navigate(
            "runs_screen"
        )
    }
    AlertDialog(
        onDismissRequest = {
            showAlert.value = false
        },
        title = title.let {
            {
                Column(
                    Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = title)
                    Divider(modifier = Modifier.padding(bottom = 8.dp))
                }
            }
        },
        text = {Text(text = "Dismiss to cancel")},

        confirmButton = {
            Button(onClick = {
                TrackingService.endRunAndSaveIntoDb.postValue(true)
                showAlert.value = false
            }) {
                Text(text = "Finish")
            }
        },

        dismissButton = {
            Button(onClick = {
                cancelRun()
                showAlert.value = false
            }) {
                Text(text = "Cancel run")
            }
        },

        modifier = Modifier.padding(vertical = 8.dp)
    )
}



