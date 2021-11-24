package com.example.pokedexapp.repository

import com.example.pokedexapp.data.models.PokedexListEntry
import com.example.pokedexapp.data.remote.PokeApi
import com.example.pokedexapp.data.remote.responses.Pokemon
import com.example.pokedexapp.data.remote.responses.PokemonList
import com.example.pokedexapp.db.PokemonDao
import com.example.pokedexapp.db.PokemonDataBase
import com.example.pokedexapp.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import java.lang.Exception
import javax.inject.Inject

@ActivityScoped
class DefaultPokemonRepository @Inject constructor(
    private val pokemonDao: PokemonDao,
    private val api: PokeApi
) : PokemonRepository {

    override suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonList(limit, offset)
        }catch (e: Exception){
            return Resource.Error("An unkown error ocurred.")
        }
        return Resource.Succes(response)
    }

    override suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        val response = try {
            api.getPokemonInfo(pokemonName )
        }catch (e: Exception){
            return Resource.Error("An unkown error ocurred.")
        }
        return Resource.Succes(response)
    }

    override fun setShouldReturnNetworkError(value: Boolean) {
        TODO("EMPTY")
    }

    override suspend fun insertFavPokemon(pokemon: PokedexListEntry) {
        pokemonDao.insertFavPokemons(pokemon)
    }

    override suspend fun getFavPokemons(pokemonList: PokemonList) {
        TODO("Not yet implemented")
    }
}