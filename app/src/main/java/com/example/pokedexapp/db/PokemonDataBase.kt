package com.example.pokedexapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [FavPokemon::class],
    version = 1,
    exportSchema = false
)
abstract class PokemonDataBase : RoomDatabase() {

    abstract fun getDAO(): PokemonDao

    companion object {
        private var dbINSTANCE: PokemonDataBase? = null

        fun getPokemonDataBase(context: Context): PokemonDataBase {
            if (dbINSTANCE == null) {
                dbINSTANCE = Room.databaseBuilder<PokemonDataBase>(
                    context.applicationContext, PokemonDataBase::class.java, "pokemon_database"
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return dbINSTANCE!!
        }
    }
}