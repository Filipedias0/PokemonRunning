package com.example.pokedexapp.data.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import androidx.test.filters.SmallTest
import com.example.pokedexapp.data.models.PokedexListEntry
import com.example.pokedexapp.db.PokemonDao
import com.example.pokedexapp.db.PokemonDataBase
import com.example.pokedexapp.getOrAwaitValue
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
class PokemonDaoTest {

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
    fun insertFavPokemon() = runBlockingTest {
        val pokemon = PokedexListEntry( pokemonName = "name", imageUrl = "url", number = 0)
        dao.insertFavPokemons(pokemon)

        val allFavPokemons = dao.observeAllFavPokemons().getOrAwaitValue()

        val insertStatus = allFavPokemons.find { pokedexListEntry -> pokedexListEntry.pokemonName == pokemon.pokemonName }

        assertThat(insertStatus).isNotNull()
    }

    @Test
    fun deleteFavPokemon() = runBlockingTest {

        dao.insertFavPokemons(PokedexListEntry( pokemonName = "test", imageUrl = "url", number = 0))

        val insertedPokemon = dao.observeAllFavPokemons().getOrAwaitValue()[0]

        dao.deleteFavPokemon(insertedPokemon)

        val insertStatus =  dao.observeAllFavPokemons().getOrAwaitValue().isNullOrEmpty()

        assertThat(insertStatus).isEqualTo(true)
    }

    @Test
    fun searchFavPokemon() = runBlockingTest {
        dao.insertFavPokemons(PokedexListEntry( pokemonName = "name", imageUrl = "url", number = 0))

        val isFav = dao.searchFavoritePokemon("name")

        val searchStatus = isFav.find { pokedexListEntry -> pokedexListEntry.pokemonName == "name" }

        assertThat(searchStatus).isNotNull()
    }
}