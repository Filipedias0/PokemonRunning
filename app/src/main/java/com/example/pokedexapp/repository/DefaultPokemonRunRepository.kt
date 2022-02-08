package com.example.pokedexapp.repository

import androidx.lifecycle.LiveData
import com.example.pokedexapp.data.models.PokedexListEntry
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

    override fun getAllRunsSortedByDate(): LiveData<List<Run>> {
        return runDAO.getAllRunsSortedByDate()
    }

    override fun getAllRunsSortedByTimeInMillis(): LiveData<List<Run>> {
        return runDAO.getAllRunsSortedByTimeInMillis()
    }

    override fun getAllRunsSortedByAvgSpeed(): LiveData<List<Run>> {
        return runDAO.getAllRunsSortedByAvgSpeed()
    }

    override fun getAllRunsSortedByCaloriesBurned(): LiveData<List<Run>> {
        return runDAO.getAllRunsSortedByCaloriesBurned()
    }

    override fun getAllRunsSortedByDistance(): LiveData<List<Run>> {
        return runDAO.getAllRunsSortedByDistance()
    }

    override fun getTotalTimeInMillis(): LiveData<Long> {
        return runDAO.getTotalTimeInMillis()
    }

    override fun getTotalCaloriesBurned(): LiveData<Int> {
        return runDAO.getTotalCaloriesBurned()
    }

    override fun getTotalDistance(): LiveData<Int> {
        return runDAO.getTotalDistance()
    }

    override fun getTotalAvgSpeed(): LiveData<Float> {
        return runDAO.getTotalAvgSpeed()
    }
}