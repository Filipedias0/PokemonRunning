package com.example.pokedexapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favPokemons")
class FavPokemon(

    @PrimaryKey(autoGenerate = true)@ColumnInfo(name = "id") val id: Int= 0,
    @ColumnInfo(name = "pokemonName")val pokemonName: String,
    @ColumnInfo(name = "imageUrl")val imageUrl: String,
    @ColumnInfo(name = "number")val number: Int
)
