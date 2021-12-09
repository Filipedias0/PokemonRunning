package com.example.pokedexapp.data.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.example.pokedexapp.data.models.PokedexListEntry
import com.example.pokedexapp.db.PokemonDao
import com.example.pokedexapp.db.PokemonDataBase
import com.example.pokedexapp.getOrAwaitValue
import com.google.common.truth.Truth
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
    private lateinit var dao: PokemonDao

    @Before
    fun setup(){
        hiltRule.inject()
        dao = database.pokemonDao()
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun insertRun() = runBlockingTest {
        TODO("TODO")
    }

    @Test
    fun deleteRun() = runBlockingTest {
        TODO("TODO")
    }

    @Test
    fun getAllRunsSortedByDate() = runBlockingTest {
        TODO("TODO")
    }

    @Test
    fun getAllRunsSortedByTimeInMillis() = runBlockingTest {
        TODO("TODO")
    }

    @Test
    fun getAllRunsSortedByAvgSpeed() = runBlockingTest {
        TODO("TODO")
    }

    @Test
    fun getAllRunsSortedByDistance() = runBlockingTest {
        TODO("TODO")
    }

    @Test
    fun getTotalTimeInMillis() = runBlockingTest {
        TODO("TODO")
    }

    @Test
    fun getTotalCaloriesBurned() = runBlockingTest {
        TODO("TODO")
    }

    @Test
    fun getTotalDistance() = runBlockingTest {
        TODO("TODO")
    }

    @Test
    fun getTotalAvgSpeed() = runBlockingTest {
        TODO("TODO")
    }
}