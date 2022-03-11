package com.example.pokedexapp.runningSection.runsScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pokedexapp.R
import com.example.pokedexapp.db.Run
import com.example.pokedexapp.other.SortType
import com.example.pokedexapp.other.TrackingUtility
import com.example.pokedexapp.util.DropDown
import com.example.pokedexapp.util.PermissionsHandler
import com.google.accompanist.permissions.ExperimentalPermissionsApi
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
            Image(
                painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                contentDescription = "Pokemon",
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            PermissionsHandler(showAlert = showAlert)
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
                navController = navController,
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun RunningWrapper(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: RunsViewModel,
) {
    val sortByState = remember { mutableStateOf(viewModel.sortByState) }
    val sortByText = viewModel.sortByState.observeAsState()
    val options = listOf("Date", "Distance", "Running time", "Avg Speed", "Calories burned")
    val runs by viewModel.runsMediator.observeAsState(listOf())
    val lifecycleOwner = LocalLifecycleOwner.current
    val isLoading = viewModel.loading.value

    fun subscribeToObservers() {
        sortByState.value.observe(lifecycleOwner) { sortBy ->
            when (sortBy) {
                options[0] -> viewModel.sortRuns(SortType.DATE)
                options[1] -> viewModel.sortRuns(SortType.DISTANCE)
                options[2] -> viewModel.sortRuns(SortType.RUNNING_TIME)
                options[3] -> viewModel.sortRuns(SortType.AVG_SPEED)
                options[4] -> viewModel.sortRuns(SortType.CALORIES_BURNED)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier,
    ) {
        subscribeToObservers()

        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 5.dp,
                )
        ) {

            DropDown(
                text = "Sort By: ${sortByText.value}",
                options = options,
                sortByState = sortByState.value,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
            )

                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier
                        .size(36.dp)
                        .offset(y = 4.dp)
                        .clickable {
                            navController.navigate("settings_screen")
                        }
                )

                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = "Statistics screen",
                    tint = Color.White,
                    modifier = Modifier
                        .size(36.dp)
                        .offset(y = 4.dp)
                        .clickable {
                            navController.navigate("statistics_screen")
                        }
                )
        }
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .offset(y = 120.dp)
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(0.5f)
            )
        }

        Box(
            contentAlignment = Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(vertical = 12.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {

                items(runs) { item ->
                    RunSection(item)
                    Spacer(modifier = Modifier.height(22.dp))
                    if (runs.last() == item) {
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        bottom = 12.dp,
                        end = 6.dp
                    )
            ) {
                FloatingActionButton(
                    elevation = FloatingActionButtonDefaults.elevation(12.dp, 12.dp),
                    onClick = {
                        navController.navigate(
                            "start_run_screen"
                        )
                    },
                    backgroundColor = Color(255, 203, 8),
                    contentColor = Color(0, 103, 180),
                    modifier = Modifier
                        .zIndex(2f)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Run")
                }
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
    ) {
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
            Text(color = Color(0, 103, 180), text = run.avgSpeedInKMH.toString() + "km/h")
            Text(color = Color(0, 103, 180), text = run.caloriesBurned.toString() + "Kcal")
            Text(color = Color(0, 103, 180), text = run.distanceInMeters.toString() + "m")
            Text(
                color = Color(0, 103, 180),
                text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis) + "ms"
            )
        }
    }
}



