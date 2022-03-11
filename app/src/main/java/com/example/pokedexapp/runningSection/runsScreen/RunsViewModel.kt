package com.example.pokedexapp.runningSection.runsScreen

import androidx.compose.runtime.mutableStateOf
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
    repository: PokemonRunRepository
) : ViewModel() {

    private val runsSortedByDate = repository.getAllRunsSortedByDate()
    private val runsSortedByDistance = repository.getAllRunsSortedByDistance()
    private val runsSortedByCaloriesBurned = repository.getAllRunsSortedByCaloriesBurned()
    private val runsSortedByTimeInMillis = repository.getAllRunsSortedByTimeInMillis()
    private val runsSortedByAvgSpeed = repository.getAllRunsSortedByAvgSpeed()
    var sortByState = MutableLiveData("Date")
    var loading = mutableStateOf(false)

    val runsMediator = MediatorLiveData<List<Run>>()

    private var sortType = SortType.DATE

    init {
        loading.value = true

        runsMediator.addSource(runsSortedByDate) { result ->
            if(sortType == SortType.DATE){
                loading.value = false
                result?.let { runsMediator.value = it }
            }
        }

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
        SortType.DATE -> runsSortedByDate.value?.let{ runsMediator.value = it }
        SortType.RUNNING_TIME -> runsSortedByTimeInMillis.value?.let{ runsMediator.value = it }
        SortType.AVG_SPEED -> runsSortedByAvgSpeed.value?.let{ runsMediator.value = it }
        SortType.DISTANCE -> runsSortedByDistance.value?.let{ runsMediator.value = it }
        SortType.CALORIES_BURNED -> runsSortedByCaloriesBurned.value?.let{ runsMediator.value = it }
    }.also {
        this.sortType = sortType
    }

}