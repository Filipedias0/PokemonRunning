package com.example.pokedexapp.runningSection.startRunScreen

import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {

    @set:Inject
    var injectWeight = 80f

    fun insertRun(run: Run) = viewModelScope.launch{
        repository.insertRun(run)
    }

    companion object {
        var saveRun = MutableLiveData<Run>(null)
        var weight = 80F
    }
}