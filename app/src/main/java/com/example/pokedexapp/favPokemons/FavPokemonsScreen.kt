package com.example.pokedexapp.favPokemons

import android.os.Build
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.pokedexapp.R
import com.example.pokedexapp.data.models.PokedexListEntry
import com.example.pokedexapp.util.PokemonText
import com.example.pokedexapp.ui.theme.RobotoCondensed

@Composable
fun FavPokemonsScreen(
    navController: NavController,
    viewModel: FavPokemonsViewModel = hiltViewModel()
){
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                contentDescription = "Pokemon",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            PokemonList(navController = navController)
        }
    }

}

@Composable
fun PokemonList(
    navController: NavController,
    viewModel: FavPokemonsViewModel = hiltViewModel()
){
    val pokemonList : List<PokedexListEntry> by viewModel.favPokemons.observeAsState(initial = listOf())

    Column(
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 20.dp
            )
            .border(
                width = 2.dp,
                color = Color(255, 203, 8),
                shape = RoundedCornerShape(10.dp)
            )
            .shadow(10.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colors.secondary)
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        PokemonText(
            text = "Favorite Pokémons",
            modifier = Modifier
                .fillMaxWidth()
        )

        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            val itemCount = if (pokemonList.size % 2 == 0) {
                pokemonList.size / 2
            } else {
                pokemonList.size / 2 + 1
            }
            items(itemCount) {
                PokedexRow(rowIndex = it, entries = pokemonList, navController = navController)
            }
        }
    }
}

@ExperimentalCoilApi
@Composable
fun PokedexEntry(
    entry: PokedexListEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: FavPokemonsViewModel = hiltViewModel()
){
    val defaultDominantColor = MaterialTheme.colors.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Box(
        contentAlignment = Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        defaultDominantColor
                    )
                )
            )
            .border(
                width = 2.dp,
                color = Color(255, 203, 8),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable {
                navController.navigate(
                    "pokemon_detail_screen/${dominantColor.toArgb()}/${entry.pokemonName}"
                )
            }
    ){
        Column {
            val imageLoader = ImageLoader.Builder(LocalContext.current)
                .crossfade(true)
                .build()

            val request = ImageRequest.Builder(LocalContext.current)
                .data(entry.imageUrl)
                .target{
                    viewModel.calcDominantColor(it){ color ->
                        dominantColor = color
                    }
                }
                .build()

            imageLoader.enqueue(request)

            val gifLoader = ImageLoader.Builder(LocalContext.current)
                .componentRegistry {
                    if (Build.VERSION.SDK_INT >= 28) {
                        add(ImageDecoderDecoder(LocalContext.current))
                    } else {
                        add(GifDecoder())
                    }
                }
                .build()

            val painter = rememberImagePainter(entry.imageUrl , gifLoader)
            Image(
                painter = painter,
                contentDescription = entry.pokemonName,
                modifier = Modifier
                    .size(120.dp)
                    .align(CenterHorizontally)
            )

            Text(
                text = entry.pokemonName,
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PokedexRow(
    rowIndex: Int,
    entries: List<PokedexListEntry>,
    navController: NavController
){
    Column {
        Row{
            PokedexEntry(
                entry = entries[rowIndex * 2],
                navController = navController,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(16.dp))
            if(entries.size >= rowIndex * 2 + 2){
                PokedexEntry(
                    entry = entries[rowIndex * 2 + 1],
                    navController = navController,
                    modifier = Modifier.weight(1f),
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

