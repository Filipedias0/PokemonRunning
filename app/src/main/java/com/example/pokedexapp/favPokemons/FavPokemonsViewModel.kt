package com.example.pokedexapp.favPokemons

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pokedexapp.db.FavPokemon
import com.example.pokedexapp.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavPokemonsViewModel @Inject constructor(
    private val repository: PokemonRepository
): ViewModel() {

    lateinit var favPokemons: MutableLiveData<List<FavPokemon>>

    init {
        favPokemons = MutableLiveData()
        loadFavPokemons()
    }

    fun getAllFavPokemonsObserver(): MutableLiveData<List<FavPokemon>>{
        return favPokemons
    }

    fun loadFavPokemons(){
        val list = repository.getAllFavPokemons()
        favPokemons.postValue(list)
    }

}