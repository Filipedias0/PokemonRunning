package com.example.pokedexapp.repository

import androidx.lifecycle.LiveData
import com.example.pokedexapp.data.remote.responses.PokemonList
import com.example.pokedexapp.db.Run
import com.example.pokedexapp.util.Resource

interface PokemonRunRepository {
    suspend fun insertRun(run: Run)

    suspend fun deleteRun(run: Run)

    suspend fun getAllRunsSortedByDate(): LiveData<List<Run>>

    suspend fun getAllRunsSortedByTimeInMillis() : LiveData<List<Run>>

    suspend fun getAllRunsSortedByAvgSpeed() : LiveData<List<Run>>

    suspend fun getAllRunsSortedByDistance() : LiveData<List<Run>>

    suspend fun getTotalTimeInMillis(): LiveData<Long>

    suspend fun getTotalCaloriesBurned(): LiveData<Int>

    suspend fun getTotalDistance(): LiveData<Int>

    suspend fun getTotalAvgSpeed(): LiveData<Float>

}