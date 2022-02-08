package com.example.pokedexapp.repository

import androidx.lifecycle.LiveData
import com.example.pokedexapp.data.remote.responses.PokemonList
import com.example.pokedexapp.db.Run
import com.example.pokedexapp.util.Resource

interface PokemonRunRepository {
    suspend fun insertRun(run: Run)

    suspend fun deleteRun(run: Run)

    fun getAllRunsSortedByDate(): LiveData<List<Run>>

    fun getAllRunsSortedByTimeInMillis() : LiveData<List<Run>>

    fun getAllRunsSortedByAvgSpeed() : LiveData<List<Run>>

    fun getAllRunsSortedByDistance() : LiveData<List<Run>>

    fun getAllRunsSortedByCaloriesBurned() : LiveData<List<Run>>

    fun getTotalTimeInMillis(): LiveData<Long>

    fun getTotalCaloriesBurned(): LiveData<Int>

    fun getTotalDistance(): LiveData<Int>

    fun getTotalAvgSpeed(): LiveData<Float>

}