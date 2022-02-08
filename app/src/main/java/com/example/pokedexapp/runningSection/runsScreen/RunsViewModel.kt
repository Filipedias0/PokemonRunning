package com.example.pokedexapp.runningSection.runsScreen

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pokedexapp.db.Run
import com.example.pokedexapp.other.SortType
import com.example.pokedexapp.repository.PokemonRunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RunsViewModel @Inject constructor(
    private val repository: PokemonRunRepository
) : ViewModel() {

    private val runsSortedByDate = repository.getAllRunsSortedByDate()
    private val runsSortedByDistance = repository.getAllRunsSortedByDistance()
    private val runsSortedByCaloriesBurned = repository.getAllRunsSortedByCaloriesBurned()
    private val runsSortedByTimeInMillis = repository.getAllRunsSortedByTimeInMillis()
    private val runsSortedByAvgSpeed = repository.getAllRunsSortedByAvgSpeed()
    var sortByState = MutableLiveData("Date")

    val runs = MediatorLiveData<List<Run>>()

    var sortType = SortType.DATE

    init {
        runs.addSource(runsSortedByDate) { result ->
            if(sortType == SortType.DATE){
                result?.let { runs.value = it }
            }
        }

        runs.addSource(runsSortedByAvgSpeed) { result ->
            if(sortType == SortType.AVG_SPEED){
                result?.let { runs.value = it }
            }
        }

        runs.addSource(runsSortedByDistance) { result ->
            if(sortType == SortType.DISTANCE){
                result?.let { runs.value = it }
            }
        }

        runs.addSource(runsSortedByCaloriesBurned) { result ->
            if(sortType == SortType.CALORIES_BURNED){
                result?.let { runs.value = it }
            }
        }

        runs.addSource(runsSortedByTimeInMillis) { result ->
            if(sortType == SortType.RUNNING_TIME){
                result?.let { runs.value = it }
            }
        }
    }

    fun sortRuns(sortType: SortType) = when(sortType){
        SortType.DATE -> runsSortedByDate.value?.let{ runs.value = it }
        SortType.RUNNING_TIME -> runsSortedByTimeInMillis.value?.let{ runs.value = it }
        SortType.AVG_SPEED -> runsSortedByAvgSpeed.value?.let{ runs.value = it }
        SortType.DISTANCE -> runsSortedByDistance.value?.let{ runs.value = it }
        SortType.CALORIES_BURNED -> runsSortedByCaloriesBurned.value?.let{ runs.value = it }
    }.also {
        this.sortType = sortType
    }

}