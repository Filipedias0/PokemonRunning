package com.example.pokedexapp.pokemondetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedexapp.data.models.PokedexListEntry
import com.example.pokedexapp.data.remote.responses.Pokemon
import com.example.pokedexapp.repository.PokemonRepository
import com.example.pokedexapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    var isFavPokemon: MutableLiveData<Boolean> = MutableLiveData()

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        val pokemonInfo = repository.getPokemonInfo(pokemonName)

        getIsFavPokemon(pokemonName)

        return pokemonInfo
    }

    fun isFavPokemonObserver():MutableLiveData<Boolean>{
        return isFavPokemon
    }

    fun updateFavPokemon(pokemon: PokedexListEntry, isFavPokemon : Boolean){
        viewModelScope.launch {
            if (isFavPokemon){
                repository.deleteFavPokemon(pokemon)
            }else {
                repository.insertFavPokemon(pokemon)
            }

            getIsFavPokemon(pokemon.pokemonName)
        }
    }

    private suspend fun getIsFavPokemon(pokemonName: String){
        val list = repository.searchFavPokemons(pokemonName)
        list.size
        isFavPokemon.postValue(!list.isNullOrEmpty())
    }
}