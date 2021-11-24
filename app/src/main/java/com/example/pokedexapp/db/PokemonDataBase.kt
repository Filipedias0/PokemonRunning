package com.example.pokedexapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pokedexapp.data.models.PokedexListEntry

@Database(
    entities = [PokedexListEntry::class],
    version = 1,
    exportSchema = false
)
abstract class PokemonDataBase : RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao
}