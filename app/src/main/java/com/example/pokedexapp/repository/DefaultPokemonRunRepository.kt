package com.example.pokedexapp.repository

import androidx.lifecycle.LiveData
import com.example.pokedexapp.db.Run
import com.example.pokedexapp.db.RunDAO
import javax.inject.Inject

class DefaultPokemonRunRepository @Inject constructor(
    private val runDAO: RunDAO,
): PokemonRunRepository {

    override suspend fun insertRun(run: Run) {
        return runDAO.insertRun(run)
    }

    override suspend fun deleteRun(run: Run) {
        return runDAO.deleteRun(run)
    }

    override suspend fun getAllRunsSortedByDate(): LiveData<List<Run>> {
        return runDAO.getAllRunsSortedByDate()
    }

    override suspend fun getAllRunsSortedByTimeInMillis(): LiveData<List<Run>> {
        return runDAO.getAllRunsSortedByTimeInMillis()
    }

    override suspend fun getAllRunsSortedByAvgSpeed(): LiveData<List<Run>> {
        return runDAO.getAllRunsSortedByAvgSpeed()
    }

    override suspend fun getAllRunsSortedByDistance(): LiveData<List<Run>> {
        return runDAO.getAllRunsSortedByDistance()
    }

    override suspend fun getTotalTimeInMillis(): LiveData<Long> {
        return runDAO.getTotalTimeInMillis()
    }

    override suspend fun getTotalCaloriesBurned(): LiveData<Int> {
        return runDAO.getTotalCaloriesBurned()
    }

    override suspend fun getTotalDistance(): LiveData<Int> {
        return runDAO.getTotalDistance()
    }

    override suspend fun getTotalAvgSpeed(): LiveData<Float> {
        return runDAO.getTotalAvgSpeed()
    }
}