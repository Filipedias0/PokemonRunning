package com.example.pokedexapp.pokemonList

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
import com.example.pokedexapp.util.constants.Constants.PAGE_SIZE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository,
) : ViewModel() {

    private var curPage = 0

    var pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())
    var loadStatus = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    var cachedPokemonList = listOf<PokedexListEntry>()
    private var isSearchStarting = true
    var isSearching = mutableStateOf(false)

    init {
        loadPokemonPaginated()
    }

      fun searchPokemonList(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                pokemonList.value = cachedPokemonList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }

            if(isSearchStarting){
                isSearchStarting = false
                loadStatus.value = ""
            }

            isLoading.value = true
            delay(100)
            repository.getPokemonInfo(query.lowercase(Locale.getDefault())).let {

                if(loadStatus.value == "Success"){
                    return@launch
                }
                if(it.data == null){
                    loadStatus.value = "Nenhum Pok??mon encontrado"
                    isLoading.value = false
                    pokemonList.value = listOf()
                    return@launch
                }else{
                    pokemonList.value = listOf(PokedexListEntry(pokemonName = it.data.name, imageUrl = it.data.sprites.front_default, number = it.data.id))
                    isLoading.value = false
                    loadStatus.value = "Success"
                }
            }
        }
    }

     fun returnNetworkError(){
        repository.setShouldReturnNetworkError(true)
    }

    fun loadPokemonPaginated() {
        if (pokemonList.value.size == 1){
            return
        }
        Timber.d("Searching")
        viewModelScope.launch {
            isLoading.value = true

            val result = repository.getPokemonList(PAGE_SIZE, curPage * PAGE_SIZE)
            when ((result)) {
                is Resource.Succes -> {
                    endReached.value = curPage * PAGE_SIZE >= result.data!!.count
                    val pokedexEntries = result.data.results.mapIndexed { _, entry ->
                        val number = if (entry.url.endsWith("/")) {
                            entry.url.dropLast(1).takeLastWhile {
                                it.isDigit()
                            }
                        } else {
                            entry.url.takeLastWhile { it.isDigit() }
                        }
                        val url =
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokedexListEntry(
                            pokemonName = entry.name.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.ROOT
                                ) else it.toString()
                            },
                            imageUrl = url,
                            number = number.toInt()
                        )
                    }
                    curPage++

                    loadStatus.value = ""
                    isLoading.value = false
                    cachedPokemonList = cachedPokemonList + pokedexEntries
                    pokemonList.value += pokedexEntries
                }
                is Resource.Error -> {
                    loadStatus.value = result.message!!
                    isLoading.value = false
                }

            }
        }
    }

    fun calcDominantColor(
        drawable: Drawable,
        onFinish: (Color) -> Unit
    ) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmp).generate { pallete ->
            pallete?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}