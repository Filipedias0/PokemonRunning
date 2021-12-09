package com.example.pokedexapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pokedexapp.data.models.PokedexListEntry

@Database(
    entities = [
        PokedexListEntry::class,
        Run::class
               ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PokemonDataBase : RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao
    abstract fun runDao(): RunDAO
}