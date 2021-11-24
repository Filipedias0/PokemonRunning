package com.example.pokedexapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.pokedexapp.data.models.PokedexListEntry

@Dao
interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavPokemons(pokemon: PokedexListEntry): Long

    @Query("SELECT * FROM favPokemons")
    fun observeAllFavPokemons(): LiveData<List<PokedexListEntry>>

}