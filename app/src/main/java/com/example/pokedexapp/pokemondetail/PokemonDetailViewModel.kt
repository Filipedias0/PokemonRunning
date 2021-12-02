package com.example.pokedexapp.pokemondetail

import androidx.lifecycle.ViewModel
import com.example.pokedexapp.data.remote.responses.Pokemon
import com.example.pokedexapp.db.FavPokemon
import com.example.pokedexapp.favPokemons.FavPokemonsViewModel
import com.example.pokedexapp.repository.DefaultPokemonRepository
import com.example.pokedexapp.repository.PokemonRepository
import com.example.pokedexapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        return repository.getPokemonInfo(pokemonName)
    }
<<<<<<< Updated upstream
=======

    fun insertFavPokemon(favPokemon: FavPokemon){
        viewModelScope.launch {
            repository.insertFavPokemon(favPokemon)
        }
    }
>>>>>>> Stashed changes
}