package com.example.pokedexapp.favPokemons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pokedexapp.R
import com.example.pokedexapp.data.models.PokedexListEntry
import com.example.pokedexapp.pokemonList.PokedexRow
import com.example.pokedexapp.pokemonList.PokemonListViewModel
import com.example.pokedexapp.pokemonList.RetrySection

@Composable
fun FavPokemonsScreen(
    navController: NavController,
    viewModel: FavPokemonsViewModel = hiltViewModel()
){
    val pokedexListEntry : List<PokedexListEntry> by viewModel.favPokemons.observeAsState(initial = listOf())
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
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            PokemonList(navController = navController, pokedexListEntry)
        }
    }

}

@Composable
fun PokemonList(
    navController: NavController,
    favPokemon: List<PokedexListEntry>,
    viewModel: PokemonListViewModel = hiltViewModel()
){
    val pokemonList by remember { viewModel.pokemonList }
    val endReached by remember { viewModel.endReached }
    val loadError by remember { viewModel.loadStatus }
    val isLoading by remember { viewModel.isLoading }
    val isSearching by remember { viewModel.isSearching }

    LazyColumn(contentPadding = PaddingValues(16.dp)){
        val itemCount = if(pokemonList.size % 2 == 0){
            pokemonList.size / 2
        }else{
            pokemonList.size /2 + 1
        }
        items(itemCount){
            if( it >= itemCount - 1 && !endReached && !isLoading && !isSearching){
                LaunchedEffect(key1 = true){
                    viewModel.loadPokemonPaginated()
                }
            }
            Text(text = favPokemon[0].pokemonName)
            PokedexRow(rowIndex = it, entries = pokemonList, navController = navController)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if(isLoading){
            CircularProgressIndicator( color = MaterialTheme.colors.primary )
        }
        if(loadError.isNotEmpty()){
            RetrySection(error = loadError) {
                viewModel.loadPokemonPaginated()

            }
        }
    }
}