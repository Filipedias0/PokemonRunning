package com.example.pokedexapp.db

import androidx.room.*

@Dao
interface PokemonDao {

    @Insert
    suspend fun insertFavPokemons(favPokemon: FavPokemon)

    @Query("SELECT * FROM favPokemons")
    fun getAllFavPokemons(): List<FavPokemon>

}