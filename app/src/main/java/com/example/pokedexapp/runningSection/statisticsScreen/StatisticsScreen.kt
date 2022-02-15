package com.example.pokedexapp.runningSection.statisticsScreen

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Settings
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
            )
        }
    }
}

@Composable
fun RunningWrapper(
    modifier: Modifier = Modifier,
) {
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
                    text = "00:00:00",
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
                    text = "0 Km",
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
                    text = "0 Kcal",
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
                    text = "0 Km/H",
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