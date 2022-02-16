package com.example.pokedexapp.runningSection.statisticsScreen

import android.os.Build
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.pokedexapp.R
import com.example.pokedexapp.db.Run
import com.example.pokedexapp.other.SortType
import com.example.pokedexapp.other.TrackingUtility
import com.example.pokedexapp.util.PermissionsHandler
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.lang.Math.round
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalPermissionsApi
@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
        ) {
            Image(
                    painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                    contentDescription = "Pokemon",
                    modifier = Modifier
                        .fillMaxWidth()
                )
            Spacer(modifier = Modifier.height(16.dp))


            RunningWrapper(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                    .shadow(10.dp, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colors.secondary)
                    .padding(16.dp),
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun RunningWrapper(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel
) {
    val totalTime = viewModel.totalTimeRun.observeAsState().value?.let {
        TrackingUtility.getFormattedStopWatchTime(it)
    } ?: "00:00:00"
    val totalAvgSpeed = viewModel.totalAvgSpeed.observeAsState().value?.let {
        val avgSpeed = round(it * 10f) / 10f
        "${avgSpeed}km/h"
    }
    val totalCaloriesBurned by viewModel.totalCaloriesBurned.observeAsState("0kcal")
    val totalDistance = viewModel.totalDistance.observeAsState().value?.let {
        val km = it / 1000f
        val totalDistance = Math.round(km * 10f) / 10f
        "${totalDistance}km"
    } ?: "0km"
    val runsSortedByDate by viewModel.runsSortedByDate.observeAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier,
    ) {

        var gifLoader = ImageLoader.Builder(LocalContext.current)
            .componentRegistry {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder(LocalContext.current))
                } else {
                    add(GifDecoder())
                }
            }
            .build()

        val painter = rememberImagePainter(R.drawable.rapidash, gifLoader)

        Text(
            text = "Statistics",
            fontSize = 42.sp,
            fontWeight = FontWeight.Medium,
            color = Color(255, 203, 8),
        )

        Spacer(Modifier.height(12.dp))

        Image(
            painter = painter,
            contentDescription = "Pokemon Rapidash",
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(22.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp
                )
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = totalTime,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(255, 203, 8),
                )

                Text(
                    text = "Total time",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0, 103, 180),
                )
            }


            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = totalDistance,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(255, 203, 8),
                )

                Text(
                    text = "Total distance",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0, 103, 180),
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ){
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = totalCaloriesBurned.toString()+"kcal",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(255, 203, 8),
                )

                Text(
                    text = "Calories burned",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0, 103, 180),
                )
            }

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = totalAvgSpeed.toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(255, 203, 8),
                )

                Text(
                    text = "Average speed",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0, 103, 180),
                )
            }
        }
    }
}