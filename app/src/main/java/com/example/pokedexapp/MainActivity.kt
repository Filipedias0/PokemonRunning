package com.example.pokedexapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokedexapp.favPokemons.FavPokemonsScreen
import com.example.pokedexapp.pokemonList.PokemonListScreen
import com.example.pokedexapp.pokemondetail.PokemonDetailScreen
import com.example.pokedexapp.runningSection.runsScreen.RunsScreen
import com.example.pokedexapp.ui.theme.PokedexAppTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pokedexapp.runningSection.startRunScreen.StartRunScreen
import com.example.pokedexapp.runningSection.welcome.WelcomeScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalPermissionsApi
    @ExperimentalMaterialApi
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
                    Navigation(navController = navController)
                }
            }
        }
    }
}



@ExperimentalPermissionsApi
@Composable
fun Navigation(navController: NavHostController){
        NavHost(
            navController = navController,
            startDestination = "pokemon_list_screen"
        ) {
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
                    pokemonName = pokemonName?.toLowerCase(Locale.ROOT) ?: "",
                    navController = navController
                )
            }
            composable("fav_pokemons_screen"){
                FavPokemonsScreen(navController = navController)
            }

            composable("welcome_screen"){
                WelcomeScreen(navController = navController)
            }

            composable("runs_screen") {
                RunsScreen(navController = navController)
            }

            composable("start_run_screen") {
                StartRunScreen(navController = navController)
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