package com.example.pokedexapp.runningSection.statisticsScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.pokedexapp.R
import com.example.pokedexapp.db.Run
import com.example.pokedexapp.other.SortType
import com.example.pokedexapp.other.TrackingUtility
import com.example.pokedexapp.util.DropDown
import com.example.pokedexapp.util.PokemonText
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlin.math.roundToInt

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
                    .border(
                        width = 2.dp,
                        color = Color(255, 203, 8),
                        shape = RoundedCornerShape(10.dp)
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
        val avgSpeed = (it * 10f).roundToInt() / 10f
        "$avgSpeed km/h"
    }
    val totalCaloriesBurned by viewModel.totalCaloriesBurned.observeAsState("0kcal")
    val totalDistance = viewModel.totalDistance.observeAsState().value?.let {
        val km = it / 1000f
        val totalDistance = (km * 10f).roundToInt() / 10f
        "$totalDistance km"
    } ?: "0km"
    val sortByState = remember { mutableStateOf(viewModel.sortByState) }
    val sortByText = viewModel.sortByState.observeAsState()
    val options = listOf("Distance", "Running time", "Avg Speed", "Calories burned")
    val runs = viewModel.runsMediator.observeAsState(listOf())
    val lifecycleOwner = LocalLifecycleOwner.current

    fun subscribeToObservers() {
        sortByState.value.observe(lifecycleOwner) { sortBy ->
            when (sortBy) {
                options[0] -> viewModel.sortRuns(SortType.DISTANCE)
                options[1] -> viewModel.sortRuns(SortType.RUNNING_TIME)
                options[2] -> viewModel.sortRuns(SortType.AVG_SPEED)
                options[3] -> viewModel.sortRuns(SortType.CALORIES_BURNED)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .verticalScroll(
                state = rememberScrollState(),
            ),
    ) {
        subscribeToObservers()

        val gifLoader = ImageLoader.Builder(LocalContext.current)
            .componentRegistry {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder(LocalContext.current))
                } else {
                    add(GifDecoder())
                }
            }
            .build()

        val painter = rememberImagePainter(R.drawable.rapidash, gifLoader)

        PokemonText(
            text = "Statistics",
            modifier = Modifier
                .fillMaxWidth()
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

                PokemonText(
                    text = totalTime,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
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

                PokemonText(
                    text = totalDistance,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
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
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PokemonText(
                    text = "$totalCaloriesBurned kcal",
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
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

                PokemonText(
                    text = totalAvgSpeed.toString(),
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                )

                Text(
                    text = "Average speed",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0, 103, 180),
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                DropDown(
                    text = "${sortByText.value} ${
                        if (sortByText.value == "Running time") "evolution"
                        else "over time"
                    }",
                    options = options,
                    sortByState = sortByState.value,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                )

                Icon(
                    imageVector = Icons.Default.Timeline, contentDescription = "Filter",
                    tint = Color(255, 203, 8),
                    modifier = Modifier
                        .size(36.dp)
                        .offset(y = 4.dp)
                )

            }
        }

        LineChartWithShadow(getDataPoints(runs.value))

        Spacer(modifier = Modifier.height(22.dp))
    }
}

data class DataPoint(
    val x: Float,
    val y: Float
)

fun List<DataPoint>.xMax(): Float = maxByOrNull { it.x }?.x ?: 0f
fun List<DataPoint>.yMax(): Float = maxByOrNull { it.y }?.y ?: 0f

fun Float.toRealX(xMax: Float, width: Float) = (this / xMax) * width
fun Float.toRealY(yMax: Float, height: Float) = (this / yMax) * height

@Composable
fun LineChartWithShadow(
    dataPoint: List<DataPoint>
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth(1f)
            .height(100.dp)
    ) {
        val width = size.width
        val height = size.height
        val spacingOf16DpInPixels = 16.dp.toPx()
        val verticalAxisLineStartOffset = Offset(spacingOf16DpInPixels, spacingOf16DpInPixels)
        val verticalAxisLineEndOffset = Offset(spacingOf16DpInPixels, height)

        drawLine(
            Color(0, 103, 180),
            verticalAxisLineStartOffset,
            verticalAxisLineEndOffset,
            strokeWidth = Stroke.DefaultMiter
        )

        val horizontalAxisLineStartOffset = Offset(spacingOf16DpInPixels, height)
        val horizontalAxisLineEndOffset = Offset(width - spacingOf16DpInPixels, height)

        drawLine(
            Color(0, 103, 180),
            horizontalAxisLineStartOffset,
            horizontalAxisLineEndOffset,
            strokeWidth = Stroke.DefaultMiter
        )

        val xMax = dataPoint.xMax()
        val yMax = dataPoint.yMax()
        val gradientPath = Path()

        gradientPath.moveTo(spacingOf16DpInPixels, height)
        dataPoint.forEachIndexed { index, curDataPoint ->
            var normX = curDataPoint.x.toRealX(xMax, width)
            val normY = curDataPoint.y.toRealY(yMax, height)

            if (index == 0) normX += spacingOf16DpInPixels
            if (index == dataPoint.size - 1) normX -= spacingOf16DpInPixels

            if (index < dataPoint.size - 1) {
                val offsetStart = Offset(normX, normY)
                var nextNormXPoint = dataPoint[index + 1].x.toRealX(xMax, width)

                if (index == dataPoint.size - 2)
                    nextNormXPoint =
                        dataPoint[index + 1].x.toRealX(xMax, width = width) - spacingOf16DpInPixels

                val nextNormYPoint = dataPoint[index + 1].y.toRealY(yMax, height)
                val offsetEnd = Offset(nextNormXPoint, nextNormYPoint)

                drawLine(
                    Color(0, 103, 180),
                    offsetStart,
                    offsetEnd,
                    strokeWidth = Stroke.DefaultMiter
                )
            }

            if(!normX.isNaN() || !normY.isNaN()){
                drawCircle(
                    Color(255, 203, 8),
                    radius = 6.dp.toPx(),
                    Offset(normX, normY)
                )
            }

            with(
                gradientPath
            ) {
                lineTo(normX, normY)
            }
        }

        with(
            gradientPath
        ) {
            lineTo(width - spacingOf16DpInPixels, height)
            lineTo(0f, height)
            close()
            drawPath(
                this,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0, 103, 180),
                        Color(0xFFAFFFC)
                    )
                )
            )
        }
    }
}

@Composable
fun getDataPoints(runs: List<Run>): List<DataPoint> {
    val avgSpeeds = runs.map { run -> run.avgSpeedInKMH }

    return avgSpeeds.mapIndexed { i, data ->
        DataPoint(i.toFloat(), data)
    }
}