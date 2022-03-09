package com.example.pokedexapp.runningSection.statisticsScreen

import android.graphics.PointF
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.pokedexapp.R
import com.example.pokedexapp.data.models.PokedexListEntry
import com.example.pokedexapp.db.Run
import com.example.pokedexapp.other.SortType
import com.example.pokedexapp.other.TrackingUtility
import com.example.pokedexapp.util.PermissionsHandler
import com.github.mikephil.charting.data.BarEntry
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.lang.Math.round
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import kotlin.random.Random

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
        val avgSpeed = (it * 10f).roundToInt() / 10f
        "${avgSpeed}km/h"
    }
    val totalCaloriesBurned by viewModel.totalCaloriesBurned.observeAsState("0kcal")
    val totalDistance = viewModel.totalDistance.observeAsState().value?.let {
        val km = it / 1000f
        val totalDistance = (km * 10f).roundToInt() / 10f
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
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = totalCaloriesBurned.toString() + "kcal",
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
        LineChartWithShadow(getDataPoints(viewModel))
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
            .fillMaxHeight(0.5f)
    ) {
        val width = size.width
        val height = size.height
        val spacingOf16DpInPixels = 16.dp.toPx()
        val verticalAxisLineStartOffset = Offset(spacingOf16DpInPixels, spacingOf16DpInPixels)
        val verticalAxisLineEndOffset = Offset(spacingOf16DpInPixels, height)

        drawLine(
            Color.Gray,
            verticalAxisLineStartOffset,
            verticalAxisLineEndOffset,
            strokeWidth = Stroke.DefaultMiter
        )

        val horizontalAxisLineStartOffset = Offset(spacingOf16DpInPixels, height)
        val horizontalAxisLineEndOffset = Offset(width - spacingOf16DpInPixels, height)

        drawLine(
            Color.Gray,
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
            var normY = curDataPoint.y.toRealY(yMax, height)

            if (index == 0) normX += spacingOf16DpInPixels
            if (index == dataPoint.size - 1) normX -= spacingOf16DpInPixels

            if (index < dataPoint.size - 1) {
                val offsetStart = Offset(normX, normY)
                var nextNormXPoint = dataPoint[index + 1].x.toRealX(xMax, width)

                if (index == dataPoint.size - 2)
                    nextNormXPoint = dataPoint[index + 1].x.toRealX(xMax, width = width) - spacingOf16DpInPixels

                val nextNormYPoint = dataPoint[index + 1].y.toRealY(yMax, height)
                val offsetEnd = Offset(nextNormXPoint, nextNormYPoint)

                drawLine(
                    Color(0xFFFF0000).copy(alpha = 0.5f),
                    offsetStart,
                    offsetEnd,
                    strokeWidth = Stroke.DefaultMiter
                )
            }

            drawCircle(
                Color(0xFFFF0000).copy(alpha = 0.5f),
                radius = 6.dp.toPx(),
                Offset(normX, normY)
            )
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
                brush = Brush.verticalGradient(colors = listOf(
                    Color(0xFFFF0000).copy(alpha = 0.5f),
                    Color(0xFFAFFFC)
                ))
            )
        }
    }
}

@Composable
fun getDataPoints(viewModel: StatisticsViewModel): List<DataPoint> {
    val lineChartData by  viewModel.runsSortedByDate.observeAsState()
    val avgSpeeds = lineChartData?.map { run -> run.avgSpeedInKMH }

    return avgSpeeds?.mapIndexed { i, data ->
        DataPoint(i.toFloat(), data)
    } ?: listOf(DataPoint(1f, 1f))
}