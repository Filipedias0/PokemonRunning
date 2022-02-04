package com.example.pokedexapp.runningSection.runsScreen

import androidx.lifecycle.ViewModel
import com.example.pokedexapp.repository.PokemonRunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RunsViewModel @Inject constructor(
    private val repository: PokemonRunRepository
) : ViewModel() {

    val runsSortedByDate = repository.getAllRunsSortedByDate()
}