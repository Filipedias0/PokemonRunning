package com.example.pokedexapp.data.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.example.pokedexapp.data.models.PokedexListEntry
import com.example.pokedexapp.db.PokemonDao
import com.example.pokedexapp.db.PokemonDataBase
import com.example.pokedexapp.db.Run
import com.example.pokedexapp.db.RunDAO
import com.example.pokedexapp.getOrAwaitValue
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@MediumTest
@HiltAndroidTest
class RunDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: PokemonDataBase
    private lateinit var dao: RunDAO

    @Before
    fun setup(){
        hiltRule.inject()
        dao = database.runDao()
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun insertRun() = runBlockingTest {
        val run = Run( timeStamp = 0, avgSpeedInKMH = 0F, distanceInMeters = 0, timeInMillis = 0, caloriesBurned = 0)
        dao.insertRun(run)

        val allRuns = dao.getAllRunsSortedByAvgSpeed().getOrAwaitValue()

        assertThat(run).isIn(allRuns)
    }

    @Test
    fun deleteRun() = runBlockingTest {
        val run = Run( timeStamp = 0, avgSpeedInKMH = 0F, distanceInMeters = 0, timeInMillis = 0, caloriesBurned = 0)
        dao.insertRun(run)

        val insertedRun = dao.getAllRunsSortedByAvgSpeed().getOrAwaitValue()[0]

        dao.deleteRun(insertedRun)

        val validateDelete = dao.getAllRunsSortedByAvgSpeed().getOrAwaitValue().isNullOrEmpty()

        assertThat(validateDelete).isEqualTo(true)
    }

    @Test
    fun getAllRunsSortedByDate() = runBlockingTest {
        val run = Run( timeStamp = 0, avgSpeedInKMH = 0F, distanceInMeters = 0, timeInMillis = 0, caloriesBurned = 0)
        dao.insertRun(run)

        val allRuns = dao.getAllRunsSortedByDate().getOrAwaitValue()

        assertThat(run).isIn(allRuns)
    }

    @Test
    fun getAllRunsSortedByTimeInMillis() = runBlockingTest {
        val run = Run( timeStamp = 0, avgSpeedInKMH = 0F, distanceInMeters = 0, timeInMillis = 0, caloriesBurned = 0)
        dao.insertRun(run)

        val allRuns = dao.getAllRunsSortedByTimeInMillis().getOrAwaitValue()

        assertThat(run).isIn(allRuns)
    }

    @Test
    fun getAllRunsSortedByAvgSpeed() = runBlockingTest {
        val run = Run( timeStamp = 0, avgSpeedInKMH = 0F, distanceInMeters = 0, timeInMillis = 0, caloriesBurned = 0)
        dao.insertRun(run)

        val allRuns = dao.getAllRunsSortedByAvgSpeed().getOrAwaitValue()

        assertThat(run).isIn(allRuns)
    }

    @Test
    fun getAllRunsSortedByCaloriesBurned() = runBlockingTest {
        val run = Run( timeStamp = 0, avgSpeedInKMH = 0F, distanceInMeters = 0, timeInMillis = 0, caloriesBurned = 0)
        dao.insertRun(run)

        val allRuns = dao.getAllRunsSortedByCaloriesBurned().getOrAwaitValue()

        assertThat(run).isIn(allRuns)
    }

    @Test
    fun getAllRunsSortedByDistance() = runBlockingTest {
        val run = Run( timeStamp = 0, avgSpeedInKMH = 0F, distanceInMeters = 0, timeInMillis = 0, caloriesBurned = 0)
        dao.insertRun(run)

        val allRuns = dao.getAllRunsSortedByDistance().getOrAwaitValue()

        assertThat(run).isIn(allRuns)
    }

    @Test
    fun getTotalTimeInMillis() = runBlockingTest {
        val run = Run( timeStamp = 0, avgSpeedInKMH = 0F, distanceInMeters = 0, timeInMillis = 1000, caloriesBurned = 0)
        dao.insertRun(run)

        val totalTimeInMillis = dao.getTotalTimeInMillis().getOrAwaitValue()

        assertThat(totalTimeInMillis).isEqualTo(1000)
    }

    @Test
    fun getTotalCaloriesBurned() = runBlockingTest {
        val run = Run( timeStamp = 0, avgSpeedInKMH = 0F, distanceInMeters = 0, timeInMillis = 0, caloriesBurned = 1000)
        dao.insertRun(run)

        val totalCaloriesBurned = dao.getTotalCaloriesBurned().getOrAwaitValue()

        assertThat(totalCaloriesBurned).isEqualTo(1000)
    }

    @Test
    fun getTotalDistance() = runBlockingTest {
        val run = Run( timeStamp = 0, avgSpeedInKMH = 0F, distanceInMeters = 1000, timeInMillis = 0, caloriesBurned = 0)
        dao.insertRun(run)

        val totalDistance = dao.getTotalDistance().getOrAwaitValue()

        assertThat(totalDistance).isEqualTo(1000)
    }

    @Test
    fun getTotalAvgSpeed() = runBlockingTest {
        val run = Run( timeStamp = 0, avgSpeedInKMH = 20F, distanceInMeters = 0, timeInMillis = 0, caloriesBurned = 0)
        dao.insertRun(run)

        val totalAvgSpeed = dao.getTotalAvgSpeed().getOrAwaitValue()

        assertThat(totalAvgSpeed).isEqualTo(20)
    }
}