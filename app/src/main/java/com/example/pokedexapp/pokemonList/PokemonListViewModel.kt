package com.example.pokedexapp.pokemonList

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.example.pokedexapp.repository.DefaultPokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val defaultRepository : DefaultPokemonRepository
): ViewModel() {

    fun loadPokemonPaginated() : Boolean{
        return true
    }

    fun searchPokemonList(
        pokemonName: String
    ) : Boolean{
        return true
    }

    fun pokemonDetails(
        pokemonName: String
    ) : Boolean {
        return true
    }

    fun calcDominantColor(drawable: Drawable, onFinish: (androidx.compose.ui.graphics.Color) -> Unit){
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmp).generate{ pallete ->
            pallete?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}