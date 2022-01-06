package com.example.pokedexapp.runningSection.startRunScreen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.pokedexapp.R
import com.example.pokedexapp.favPokemons.FavPokemonsViewModel
import com.example.pokedexapp.util.PermissionsHandler
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.android.libraries.maps.model.PolylineOptions
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    navController: NavController
) {
    var textWeight by rememberSaveable { mutableStateOf("Text") }
    var textName by rememberSaveable { mutableStateOf("Text") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
    ) {
        GoogleMap()

        var gifLoader = ImageLoader.Builder(LocalContext.current)
            .componentRegistry {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder(LocalContext.current))
                } else {
                    add(GifDecoder())
                }
            }
            .build()

        val painter = rememberImagePainter(R.drawable.pikachu_running , gifLoader)
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
            color = Color(0,103,180),
        )

        Button(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .offset(
                    y = 30.dp
                ),
            onClick = {
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(Color(255,203,8))){
            Text(
                text = "Run!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0,103,180)
            )
        }
    }
}

@Composable
fun GoogleMap(){
    val mapView = rememberMapViewWithLifeCycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize(0.5f)
            .background(Color.White)
    ){
        AndroidView(
            {mapView}
        ){ mapView ->
            CoroutineScope(Dispatchers.Main).launch {
                val map = mapView.awaitMap()
                val pickUp = LatLng(28.7041, 77.1025)
                val destination = LatLng(12.9716, 77.5946)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 6f))
                val markerOptions = MarkerOptions()
                    .title("Delhi")
                    .position(pickUp)
                map.addMarker(markerOptions)
                val markerOptionsDestination = MarkerOptions()
                    .title("Bangalore")
                    .position(destination)
                map.addMarker(markerOptionsDestination)
                map.addPolyline(
                    PolylineOptions().add(
                        pickUp,
                        LatLng(22.2587, 71.1924),
                        LatLng(19.7515, 75.7139),
                        destination
                    )
                ).color = R.color.maps_accuracy_circle_fill_color
            }
        }
    }
}

@Composable
fun rememberMapViewWithLifeCycle(): MapView{
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = com.google.maps.android.ktx.R.id.map_frame
        }
    }
    val lifeCycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle){
        lifecycle.addObserver(lifeCycleObserver)
        onDispose {
            lifecycle.removeObserver(lifeCycleObserver)
        }
    }
    return mapView
}

@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView){
        LifecycleEventObserver{ _, event ->
            when(event){
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




