package com.example.pokedexapp.favPokemons

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.pokedexapp.data.models.PokedexListEntry
import com.example.pokedexapp.repository.PokemonRepository
import com.example.pokedexapp.util.Resource
import com.example.pokedexapp.util.constants.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FavPokemonsViewModel @Inject constructor(
    private val repository: PokemonRepository
): ViewModel() {

    val favPokemons = repository.observeFavPokemons()

    fun calcDominantColor(
        drawable: Drawable,
        onFinish: (androidx.compose.ui.graphics.Color) -> Unit
    ) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmp).generate { pallete ->
            pallete?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}