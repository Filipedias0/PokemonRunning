package com.example.pokedexapp.runningSection.startRunScreen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.pokedexapp.R
import com.example.pokedexapp.db.Run
import com.example.pokedexapp.other.TrackingUtility
import com.example.pokedexapp.runningSection.service.TrackingService
import com.example.pokedexapp.runningSection.service.sendCommandToService
import com.example.pokedexapp.util.PermissionsHandler
import com.example.pokedexapp.util.PokemonText
import com.example.pokedexapp.util.constants.Constants.ACTION_PAUSE_SERVICE
import com.example.pokedexapp.util.constants.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.pokedexapp.util.constants.Constants.ACTION_STOP_SERVICE
import com.example.pokedexapp.util.constants.Constants.MAP_ZOOM
import com.example.pokedexapp.util.constants.Constants.POLYLINE_COLOR
import com.example.pokedexapp.util.constants.Constants.POLYLINE_WIDTH
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.maps.model.LatLngBounds
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.android.libraries.maps.model.PolylineOptions
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    val textTimer = remember { mutableStateOf("00:00:00:00") }
    val lifecycleOwner = LocalLifecycleOwner.current
    var curTimeInMillis: Long
    val context = LocalContext.current
    StartRunViewModel.weight = viewModel.injectWeight

    fun subscribeToObservers() {
        TrackingService.timeRunInMillis.observe(lifecycleOwner) {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis, true)
            textTimer.value = formattedTime
        }

        StartRunViewModel.saveRun.observe(lifecycleOwner) {
            if (it != null) {
                viewModel.insertRun(it)
                StartRunViewModel.saveRun.postValue(null)
                textTimer.value = "00:00:00:00"
                TrackingService.timeRunInMillis.postValue(0L)

                navController.popBackStack()
            }
        }

        viewModel.favPokemonsLiveData.observe(lifecycleOwner) { pokemonList ->

            val randomPokemonNumber: Int = if (pokemonList.isEmpty()) 25
            else pokemonList[(pokemonList.indices).random()].number

            StartRunViewModel.pokemonNumber.postValue(randomPokemonNumber)
            val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" +
                    "versions/generation-vii/icons/${randomPokemonNumber}.png"

            val imageLoader = ImageLoader.Builder(context)
                .availableMemoryPercentage(0.25)
                .crossfade(true)
                .build()

            val request = ImageRequest.Builder(context)
                .data(url)
                .target {
                    val bmp = (it as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
                    StartRunViewModel.pokemonIconMarker.postValue(bmp)
                }
                .size(width = 160, height = 160)
                .build()

            imageLoader.enqueue(request)
        }
    }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        PermissionsHandler(showAlert = showPermissionsDialog)
        subscribeToObservers()

        if (showFinishRunDialog.value) {
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
                .border(
                    width = 2.dp,
                    color = Color(255, 203, 8),
                    shape = RoundedCornerShape(10.dp)
                )
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colors.secondary),
            showFinishRunDialog = showFinishRunDialog,
            textTimer = textTimer,
        )
    }

}

@Composable
fun RunningWrapper(
    modifier: Modifier = Modifier,
    showFinishRunDialog: MutableState<Boolean>,
    textTimer: MutableState<String>,
) {
    val isTracking by TrackingService.isTracking.observeAsState(false)
    lateinit var context: Context

    fun toggleRun() {
        if (isTracking) {
            sendCommandToService(ACTION_PAUSE_SERVICE, context)
        } else {
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

        val gifLoader = ImageLoader.Builder(LocalContext.current)
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

        Spacer(modifier = Modifier.height(10.dp))

        PokemonText(
            text = textTimer.value,
            modifier = Modifier
                .fillMaxWidth(),
            fontSize = 94f
        )

        Button(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(40.dp)
                .offset(y = 20.dp),
            onClick = {
                toggleRun()
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(Color(255, 203, 8))
        ) {
            PokemonText(
                text = if (!isTracking) "Start" else "Stop",
                fontSize = 42f,
                modifier = Modifier
                    .fillMaxWidth()
                    .absoluteOffset(y = (-18).dp)
            )
        }

        if (isTracking) {
            Button(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(40.dp)
                    .offset(
                        y = 40.dp
                    ),
                onClick = {
                    showFinishRunDialog.value = true
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(Color(255, 203, 8))
            ) {
                PokemonText(
                    text = "Finish run",
                    fontSize = 42f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .absoluteOffset(y = (-18).dp)
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
    val weight = StartRunViewModel.weight

    val context = LocalContext.current
    var curTimeInMillis = 0L
    var userLocationMarker =
            map?.addMarker(
                MarkerOptions()
                    .title("User Pokemon")
            )


    fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints!!) {
            for (pos in polyline) {
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

    fun cancelRun() {
        sendCommandToService(context = context, command = ACTION_STOP_SERVICE)
    }

    fun endRunAndSaveToDb() {
        map?.snapshot { bmp ->
            var distanceInMeters = 0
            for (polyline in pathPoints!!) {
                distanceInMeters += TrackingUtility.calculatePolylineLenght(polyline).toInt()
            }
            val avgSpeed = round(
                (distanceInMeters / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10
            ) / 10f
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val run =
                Run(bmp, dateTimeStamp, avgSpeed, distanceInMeters, curTimeInMillis, caloriesBurned)

            StartRunViewModel.saveRun.postValue(run)
            Toast.makeText(
                context, "Run saved successfully!", LENGTH_SHORT
            ).show()
            cancelRun()
        }
    }

    fun addAllPolylines() {
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
        //use the previously created marker

        if (pathPoints!!.isNotEmpty() && pathPoints!!.last().size > 1) {
            val lastLatLng = pathPoints!!.last().last()
            val location = Location(LocationManager.GPS_PROVIDER)
            location.latitude = lastLatLng.latitude
            location.longitude = lastLatLng.longitude

            userLocationMarker?.position = (lastLatLng)
            userLocationMarker?.rotation = location.bearing

            val preLastLatLng = pathPoints!!.last()[pathPoints!!.last().size - 2]
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    fun subscribeToObservers() {
        TrackingService.pathPoints.observe(lifecycleOwner) {
            addLatestPolyline()
            moveCameraToUser()

            if (pathPoints!!.isNotEmpty() && pathPoints!!.last().size == 1) {
                val lastLatLng = pathPoints!!.last().last()
                val location = Location(LocationManager.GPS_PROVIDER)
                location.latitude = lastLatLng.latitude
                location.longitude = lastLatLng.longitude

                //Create the marker that will be animated
                val markerOptions = MarkerOptions()
                    .position(lastLatLng)
                    .rotation(location.bearing)
                    .anchor(0.5F, 0.5F)
                    .icon(
                        BitmapDescriptorFactory.fromBitmap(
                            StartRunViewModel.pokemonIconMarker.value
                        )
                    )

                userLocationMarker = map?.addMarker(markerOptions)!!

                //Starting point marker

                val imageLoader = ImageLoader.Builder(context)
                    .availableMemoryPercentage(0.25)
                    .crossfade(true)
                    .build()

                val request = ImageRequest.Builder(context)
                    .data(R.drawable.poke_ball_pin)
                    .target {
                        val bmp = (it as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

                        map?.addMarker(
                            MarkerOptions().position(lastLatLng)
                                .title("Starting point marker")
                                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                        )
                    }
                    .size(width = 80, height = 80)
                    .build()

                imageLoader.enqueue(request)


            }
        }

        TrackingService.endRunAndSaveIntoDb.observe(lifecycleOwner) {
            if (it == true) {
                zoomToSeeWholeTrack()
                endRunAndSaveToDb()
                TrackingService.endRunAndSaveIntoDb.postValue(false)
            }
        }

        TrackingService.timeRunInMillis.observe(lifecycleOwner) {
            curTimeInMillis = it
        }
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

    fun cancelRun() {
        sendCommandToService(context = context, command = ACTION_STOP_SERVICE)
        TrackingService.timeRunInMillis.postValue(0L)
        textTimer.value = "00:00:00:00"
        navController.popBackStack()
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
        text = { Text(text = "Dismiss to cancel") },

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



