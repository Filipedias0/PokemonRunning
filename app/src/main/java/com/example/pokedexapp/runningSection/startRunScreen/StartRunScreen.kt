package com.example.pokedexapp.runningSection.startRunScreen

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.pokedexapp.favPokemons.FavPokemonsViewModel
import com.example.pokedexapp.runningSection.service.Polyline
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
import com.example.pokedexapp.util.constants.Constants.MAP_ZOOM
import com.example.pokedexapp.util.constants.Constants.POLYLINE_WIDTH
import com.google.android.libraries.maps.GoogleMap
import timber.log.Timber

@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalPermissionsApi
@Composable
fun StartRunScreen(
    navController: NavController,
    viewModel: FavPokemonsViewModel = hiltViewModel()
) {
    val showAlert = remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        PermissionsHandler(showAlert = showAlert, false)
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
            navController = navController
        )
    }

}

@Composable
fun RunningWrapper(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val isTracking by TrackingService.isTracking.observeAsState(false)
    lateinit var context: Context

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
        GoogleMap()
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
            text = "00:00:00",
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
                    sendCommandToService(ACTION_START_OR_RESUME_SERVICE, context)
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
fun GoogleMap() {
    val mapView = rememberMapViewWithLifeCycle()
    var map: GoogleMap? = null
    val pathPoints by TrackingService.pathPoints.observeAsState()

    val lifecycleOwner = LocalLifecycleOwner.current

    fun addAllPolylines() {
        for (polyline in pathPoints!!) {
            val polylineOptions = PolylineOptions()
                .color(R.color.colorAccent)
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
                .color(R.color.blue)
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




