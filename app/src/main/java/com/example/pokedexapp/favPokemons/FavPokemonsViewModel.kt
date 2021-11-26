package com.example.pokedexapp.favPokemons

import androidx.lifecycle.ViewModel
import com.example.pokedexapp.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavPokemonsViewModel @Inject constructor(
    private val repository: PokemonRepository
): ViewModel() {

    val favPokemons = repository.observeFavPokemons()

}