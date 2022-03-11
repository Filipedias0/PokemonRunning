package com.example.pokedexapp.runningSection.statisticsScreen

import androidx.compose.runtime.mutableStateOf
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

    private val runsSortedByDistance = repository.getAllRunsSortedByDistance()
    private val runsSortedByCaloriesBurned = repository.getAllRunsSortedByCaloriesBurned()
    private val runsSortedByTimeInMillis = repository.getAllRunsSortedByTimeInMillis()
    private val runsSortedByAvgSpeed = repository.getAllRunsSortedByAvgSpeed()
    var sortByState = MutableLiveData("Distance")

    val runsMediator = MediatorLiveData<List<Run>>()

    private var sortType = SortType.DISTANCE

    init {

        runsMediator.addSource(runsSortedByAvgSpeed) { result ->
            if(sortType == SortType.AVG_SPEED){
                result?.let { runsMediator.value = it }
            }
        }

        runsMediator.addSource(runsSortedByDistance) { result ->
            if(sortType == SortType.DISTANCE){
                result?.let { runsMediator.value = it }
            }
        }

        runsMediator.addSource(runsSortedByCaloriesBurned) { result ->
            if(sortType == SortType.CALORIES_BURNED){
                result?.let { runsMediator.value = it }
            }
        }

        runsMediator.addSource(runsSortedByTimeInMillis) { result ->
            if(sortType == SortType.RUNNING_TIME){
                result?.let { runsMediator.value = it }
            }
        }
    }

    fun sortRuns(sortType: SortType) = when(sortType){
        SortType.RUNNING_TIME -> runsSortedByTimeInMillis.value?.let{ runsMediator.value = it }
        SortType.AVG_SPEED -> runsSortedByAvgSpeed.value?.let{ runsMediator.value = it }
        SortType.DISTANCE -> runsSortedByDistance.value?.let{ runsMediator.value = it }
        SortType.CALORIES_BURNED -> runsSortedByCaloriesBurned.value?.let{ runsMediator.value = it }
        //We don't use date here
        SortType.DATE -> TODO()
    }.also {
        this.sortType = sortType
    }
}