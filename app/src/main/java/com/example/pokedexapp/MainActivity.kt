package com.example.pokedexapp

import android.media.Image
import android.os.Build
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pokedexapp.favPokemons.FavPokemonsScreen
import com.example.pokedexapp.pokemonList.PokemonListScreen
import com.example.pokedexapp.pokemondetail.PokemonDetailScreen
import com.example.pokedexapp.runningSection.runsScreen.RunsScreen
import com.example.pokedexapp.ui.theme.PokedexAppTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pokedexapp.runningSection.settingsScreen.SettingsScreen
import com.example.pokedexapp.runningSection.startRunScreen.StartRunScreen
import com.example.pokedexapp.runningSection.statisticsScreen.StatisticsScreen
import com.example.pokedexapp.runningSection.welcome.WelcomeScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.delay
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @set:Inject
    var isFirstAppOpen = true

    @OptIn(ExperimentalPermissionsApi::class,
        ExperimentalMaterialApi::class
    )
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            PokedexAppTheme {
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(
                            items = listOf(
                                BottomNavItem(
                                    name = "Home",
                                    route = "pokemon_list_screen",
                                    icon = Icons.Default.Home
                                ),
                                BottomNavItem(
                                    name = "Favorites",
                                    route = "fav_pokemons_screen",
                                    icon = Icons.Default.Favorite
                                ),
                                BottomNavItem(
                                    name = "Run",
                                    route = "welcome_screen",
                                    icon = Icons.Default.Place
                                )
                            ),
                            navController = navController,
                            onItemClick = {
                                navController.navigate(it.route)
                            }
                        )
                    }
                ) {
                    Navigation(navController = navController, isFirstAppOpen = isFirstAppOpen)
                }
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalPermissionsApi
@Composable
fun Navigation(navController: NavHostController, isFirstAppOpen: Boolean){
        NavHost(
            navController = navController,
            startDestination = "splash_screen"
        ) {
            composable("splash_screen"){
                SplashScreen(navController = navController)
            }

            composable("pokemon_list_screen") {
                PokemonListScreen(navController = navController)
            }

            composable(
                "pokemon_detail_screen/{dominantColor}/{pokemonName}",
                arguments = listOf(
                    navArgument("dominantColor") {
                        type = NavType.IntType
                    },
                    navArgument("pokemonName") {
                        type = NavType.StringType
                    }
                )
            ) {
                val dominantColor = remember {
                    val color = it.arguments?.getInt("dominantColor")
                    color?.let { Color(it) } ?: Color.White
                }
                val pokemonName = remember {
                    it.arguments?.getString("pokemonName")
                }
                PokemonDetailScreen(
                    dominantColor = dominantColor,
                    pokemonName = pokemonName?.lowercase(Locale.ROOT) ?: "",
                    navController = navController
                )
            }
            composable("fav_pokemons_screen"){
                FavPokemonsScreen(navController = navController)
            }

            composable("welcome_screen"){
                if(isFirstAppOpen) {
                    WelcomeScreen(navController = navController)
                }else{
                    RunsScreen(navController = navController)
                }
            }

            composable("runs_screen") {
                RunsScreen(navController = navController)
            }

            composable("start_run_screen") {
                StartRunScreen(navController = navController)
            }

            composable("settings_screen") {
                SettingsScreen(navController = navController)
            }

            composable("statistics_screen") {
                StatisticsScreen(navController = navController)
            }
        }
}

@ExperimentalMaterialApi
@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit
){
    val backStackEntry = navController.currentBackStackEntryAsState()
    BottomNavigation(
        modifier = modifier,
        backgroundColor = Color.Unspecified,
        elevation = 0.dp
    ){
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            BottomNavigationItem(
                selected = selected,
                onClick = { onItemClick(item) },
                selectedContentColor = Color.Yellow,
                unselectedContentColor = Color.Gray,
                icon = {
                                Icon(
                                    modifier = Modifier.size(32.dp),
                                    imageVector = item.icon,
                                    contentDescription = item.name
                                )
                },
            )
        }
    }
}
@Composable
fun SplashScreen(
    navController: NavController
){
    val scale = remember{
        Animatable(0f)
    }
    LaunchedEffect(key1 = true){
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = {
                    OvershootInterpolator(2f).getInterpolation(it)
                }
            )
        )
        delay(1000L)
        navController.navigate("pokemon_list_screen")
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.secondary)
    ){
        Image(
            painter = painterResource(id = R.drawable.poke_ball_pin),
            contentDescription = "PokéBall pin (app icon)",
            modifier = Modifier
                .scale(scale.value)
        )

    }
}