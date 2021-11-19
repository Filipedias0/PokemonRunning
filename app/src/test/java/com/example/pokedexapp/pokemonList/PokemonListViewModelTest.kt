package com.example.pokedexapp.pokemonList

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pokedexapp.MainCoroutineRule
import com.example.pokedexapp.repository.FakePokemonRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class PokemonListViewModelTest{

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel : PokemonListViewModel

    @Before
    fun setup(){
        viewModel = PokemonListViewModel(FakePokemonRepository())
    }

    @Test
    fun `pokemon list call return network error` () {
        viewModel.returnNetworkError()
        viewModel.loadPokemonPaginated()

        val pokemonList = viewModel.pokemonList

        val error = viewModel.loadStatus.value

        assertThat(error).isNotEqualTo("Success")
        assertThat(pokemonList.value).isEmpty()
    }

    @Test
    fun `pokemon list call` () {
        viewModel.loadPokemonPaginated()

        val status = viewModel.loadStatus.value

        assertThat(status).isEqualTo("Success")
    }
}