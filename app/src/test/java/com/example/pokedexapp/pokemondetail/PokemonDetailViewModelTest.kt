package com.example.pokedexapp.pokemondetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pokedexapp.MainCoroutineRule
import com.example.pokedexapp.data.models.PokedexListEntry
import com.example.pokedexapp.repository.FakePokemonRepository
import com.example.pokedexapp.getOrAwaitValueTest
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest

import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class PokemonDetailViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: PokemonDetailViewModel

    @Before
    fun setUp() {
        viewModel = PokemonDetailViewModel(FakePokemonRepository())
    }

    @Test
    fun `pokemon info call`() {
        runBlockingTest {
            val pokemonInfo = viewModel.getPokemonInfo("Charmander")
            Truth.assertThat(pokemonInfo.data?.name).isEqualTo("Charmander")
        }
    }

    @Test
    fun `fav pokemon insert into vm updating is fav status to true`() {
        runBlockingTest {
            viewModel.updateFavPokemon(PokedexListEntry(pokemonName = "name", imageUrl = "", number = 0), false)
            val isFav = viewModel.isFavPokemon.getOrAwaitValueTest()
            Truth.assertThat(isFav).isEqualTo(true)
        }
    }

    @Test
    fun `fav pokemon delete into vm updating is fav status to false`() {
        runBlockingTest {
            viewModel.updateFavPokemon(PokedexListEntry( pokemonName = "name", imageUrl = "", number = 0), false)
            viewModel.updateFavPokemon(PokedexListEntry( pokemonName = "", imageUrl ="", number = 0), true)
            val hasBeenDeleted = viewModel.isFavPokemonObserver().getOrAwaitValueTest()

            Truth.assertThat(hasBeenDeleted).isEqualTo(false)
        }
    }

}
