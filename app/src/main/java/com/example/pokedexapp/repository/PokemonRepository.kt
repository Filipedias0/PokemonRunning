package com.example.pokedexapp.repository

import com.example.pokedexapp.data.remote.responses.Pokemon
import com.example.pokedexapp.data.remote.responses.PokemonList
<<<<<<< Updated upstream
=======
import com.example.pokedexapp.db.FavPokemon
import com.example.pokedexapp.db.PokemonDataBase
>>>>>>> Stashed changes
import com.example.pokedexapp.util.Resource

interface PokemonRepository {
    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList>
    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon>
    fun setShouldReturnNetworkError(value: Boolean)
<<<<<<< Updated upstream
=======
    suspend fun insertFavPokemon(favPokemon: FavPokemon)
    fun getAllFavPokemons(): List<FavPokemon>
>>>>>>> Stashed changes
}