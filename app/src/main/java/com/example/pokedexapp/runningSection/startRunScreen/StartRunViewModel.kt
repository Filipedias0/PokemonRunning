package com.example.pokedexapp.runningSection.startRunScreen

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedexapp.db.Run
import com.example.pokedexapp.repository.PokemonRepository
import com.example.pokedexapp.repository.PokemonRunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartRunViewModel @Inject constructor(
    private val repository: PokemonRunRepository,
    pokemonRepository: PokemonRepository
) : ViewModel() {
    val favPokemonsLiveData = pokemonRepository.observeFavPokemons()

    @set:Inject
    var injectWeight = 80f

    fun insertRun(run: Run) = viewModelScope.launch{
        repository.insertRun(run)
    }


    companion object {
        var saveRun = MutableLiveData<Run>(null)
        var weight = 80F
        val pokemonIconMarker = MutableLiveData<Bitmap>()
        val pokemonNumber = MutableLiveData<Int>()
    }
}