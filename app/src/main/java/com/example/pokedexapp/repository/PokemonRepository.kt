package com.example.pokedexapp.repository

import androidx.lifecycle.LiveData
import com.example.pokedexapp.data.models.PokedexListEntry
import com.example.pokedexapp.data.remote.responses.Pokemon
import com.example.pokedexapp.data.remote.responses.PokemonList
import com.example.pokedexapp.db.PokemonDataBase
import com.example.pokedexapp.util.Resource

interface PokemonRepository {
    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList>
    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon>
    fun setShouldReturnNetworkError(value: Boolean)
    suspend fun insertFavPokemon(pokemon: PokedexListEntry)
    fun observeFavPokemons(): LiveData<List<PokedexListEntry>>
}