package com.example.pokedexapp.runningSection.startRunScreen

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.*
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.pokedexapp.data.models.PokedexListEntry
import com.example.pokedexapp.data.remote.responses.Pokemon
import com.example.pokedexapp.db.Run
import com.example.pokedexapp.repository.DefaultPokemonRepository
import com.example.pokedexapp.repository.PokemonRepository
import com.example.pokedexapp.repository.PokemonRunRepository
import com.example.pokedexapp.runningSection.service.TrackingService
import com.example.pokedexapp.runningSection.service.sendCommandToService
import com.example.pokedexapp.util.Resource
import com.example.pokedexapp.util.constants.Constants
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class StartRunViewModel @Inject constructor(
    private val repository: PokemonRunRepository,
    private val pokemonRepository: PokemonRepository
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
    }
}