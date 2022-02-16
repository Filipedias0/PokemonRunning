package com.example.pokedexapp.runningSection.statisticsScreen

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pokedexapp.db.Run
import com.example.pokedexapp.other.SortType
import com.example.pokedexapp.repository.PokemonRunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Math.round
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: PokemonRunRepository
) : ViewModel() {
    val totalTimeRun = repository.getTotalTimeInMillis()
    val totalDistance = repository.getTotalDistance()
    val totalCaloriesBurned = repository.getTotalCaloriesBurned()
    val totalAvgSpeed = repository.getTotalAvgSpeed()

    val runsSortedByDate = repository.getAllRunsSortedByDate()
}