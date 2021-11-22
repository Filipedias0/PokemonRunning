package com.example.pokedexapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "favPokemons"

)
class PokedexListEntry (
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val pokemonName: String,
    val imageUrl: String,
    val number: Int
)