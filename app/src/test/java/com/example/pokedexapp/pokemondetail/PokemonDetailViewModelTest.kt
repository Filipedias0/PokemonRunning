package com.example.pokedexapp.pokemondetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pokedexapp.MainCoroutineRule
import com.example.pokedexapp.pokemonList.PokemonListViewModel
import com.example.pokedexapp.repository.FakePokemonRepository
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class PokemonDetailViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel : PokemonDetailViewModel

    @Before
    fun setUp() {
        viewModel = PokemonDetailViewModel(FakePokemonRepository())
    }

    @Test
     fun `pokemon info call`(){
            runBlockingTest {
                val pokemonInfo = viewModel.getPokemonInfo("Charmander")
                Truth.assertThat(pokemonInfo.data?.name).isEqualTo("Charmander")
            }
        }
    }
