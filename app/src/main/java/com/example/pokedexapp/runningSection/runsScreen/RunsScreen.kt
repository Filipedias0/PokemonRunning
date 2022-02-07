package com.example.pokedexapp.runningSection.runsScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pokedexapp.R
import com.example.pokedexapp.db.Run
import com.example.pokedexapp.other.TrackingUtility
import com.example.pokedexapp.util.PermissionsHandler
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.text.DateFormat.getDateInstance
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalPermissionsApi
@Composable
fun RunsScreen(
    navController: NavController,
    viewModel: RunsViewModel = hiltViewModel()
) {
    val showAlert = remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                    contentDescription = "Pokemon",
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            PermissionsHandler(showAlert = showAlert, false)
            RunningWrapper(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                    .shadow(10.dp, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colors.secondary)
                    .padding(16.dp),
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun RunningWrapper(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: RunsViewModel,
) {
    var sortByState = remember { mutableStateOf("Date") }
    val options = listOf("Date", "Distance")
    val runs : List<Run> by viewModel.runsSortedByDate.observeAsState(listOf())

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier,
    ) {

        DropDown(
            text = "Sort By: ${sortByState.value}",
            options = options,
            sortByState = sortByState,
            modifier = Modifier
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8F)
        ){

            itemsIndexed(runs){ _, item ->
                RunSection(item)
                Spacer(modifier = Modifier.height(22.dp))
            }
        }

        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 22.dp)
        ){
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                FloatingActionButton(
                    elevation = FloatingActionButtonDefaults.elevation(12.dp, 12.dp),
                    onClick = {
                        navController.navigate(
                            "start_run_screen"
                        )
                    },
                    backgroundColor = Color(255, 203, 8),
                    contentColor = Color(0,103,180),
                    modifier = Modifier
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Run")
                }
            }
        }
    }
}

@Composable
fun DropDown(
    text: String,
    modifier: Modifier = Modifier,
    initiallyOpened: Boolean = false,
    options: List<String>,
    sortByState: MutableState<String>
) {
    var isOpen by remember {
        mutableStateOf(initiallyOpened)
    }

    val alpha = animateFloatAsState(
        targetValue = if (isOpen) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300
        )
    )

    val rotateX = animateFloatAsState(
        targetValue = if (isOpen) 0f else -90f,
        animationSpec = tween(
            durationMillis = 300
        )
    )

    Column(
        horizontalAlignment = Start,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isOpen = !isOpen
            }
            .shadow(10.dp, RoundedCornerShape(10.dp))
            .background(Color(255, 203, 8))
            .clip(CircleShape)
            .padding(12.dp, 8.dp, 12.dp, 0.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Medium,
                color = Color(0,103,180),
                fontSize = 20.sp,
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Open or close the dropdown",
                tint = Color(0,103,180),
                modifier = Modifier
                    .scale(1f, if (isOpen) -1f else 1f)
            )

        }

        Spacer(modifier = Modifier.height(10.dp))
    }


    if(isOpen){
        Column(
            horizontalAlignment = Start,
            modifier = Modifier
                .graphicsLayer {
                    transformOrigin = TransformOrigin(0.5f, 0f)
                    rotationX = rotateX.value
                }
                .alpha(alpha.value)
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .background(Color(255, 203, 8))
                .clip(CircleShape)
                .padding(12.dp, 8.dp, 12.dp, 12.dp)
        ) {
            options.forEach {
                Text(
                    text = it,
                    fontSize = 18.sp,
                    color = Color(0,103,180),
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .clickable {
                            sortByState.value = it
                            isOpen = false
                        }
                )
            }
        }
    }
}

@Composable
fun RunSection(run: Run) {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = run.timeStamp
    }
    val dateFormat = SimpleDateFormat(
        "d/MM/yyyy",
        Locale.getDefault()
    )
    val date = dateFormat.format(calendar.time)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(10.dp))
            .background(Color(255, 203, 8))
            .padding(12.dp, 8.dp)
            ){
        Text(
            color = Color(0, 103, 180),
            text = date,
            modifier = Modifier
                .padding(bottom = 6.dp)
            )

        run.img?.asImageBitmap()?.let {
            Image(
                bitmap = it,
                contentDescription = "Completed run",
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        ) {
            Text(color = Color(0, 103, 180), text = run.avgSpeedInKMH.toString()+"km/h")
            Text(color = Color(0, 103, 180), text =run.caloriesBurned.toString()+"Kcal")
            Text(color = Color(0, 103, 180), text =run.distanceInMeters.toString()+"m")
            Text(color = Color(0, 103, 180), text =TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)+"ms")
        }
    }
}



