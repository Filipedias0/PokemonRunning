package com.example.pokedexapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pokedexapp.data.models.PokedexListEntry
import com.example.pokedexapp.data.remote.responses.*
import com.example.pokedexapp.util.Resource

class FakePokemonRepository : PokemonRepository{
private var shouldReturnNetworkError = false

    private val favPokemon = mutableListOf<PokedexListEntry>()
    private val favPokemonList = mutableListOf<PokedexListEntry>()
    private val observableFavPokemonList = MutableLiveData<List<PokedexListEntry>>(favPokemonList)

    override fun setShouldReturnNetworkError(value: Boolean){
        shouldReturnNetworkError = value
    }

    private fun refreshLiveData(){
        observableFavPokemonList.postValue(favPokemon)
    }

    override suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        return if(shouldReturnNetworkError){
            Resource.Error("An unkown error ocurred.",null)
        }else {
            return Resource.Succes(PokemonList(0 , "", 0, listOf()))
        }
    }

    override suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        return if(shouldReturnNetworkError){
            Resource.Error("An unkown error ocurred.",null)
        }else {
            return Resource.Succes(
                Pokemon(
                    listOf(), 0, listOf(), listOf(), 0, listOf(), 0,
                    false, "", listOf(), "Charmander", 0, listOf(),
                    Species(
                        "", ""
                    ),
                    Sprites(
                        "", "", "",
                        "", "", "",
                        "", "",
                        Other(
                            DreamWorld("", ""),
                            Home("", "", "", ""),
                            OfficialArtwork("")
                        ),
                        Versions(
                            GenerationI(
                                RedBlue("", "", "", ""),
                                Yellow("", "", "", "")
                            ),
                            GenerationIi(
                                Crystal("", "", "", ""),
                                Gold("", "", "", ""),
                                Silver("", "", "", "")
                            ),
                            GenerationIii(
                                Emerald("", ""),
                                FireredLeafgreen("", "", "", ""),
                                RubySapphire("", "", "", "")
                            ),
                            GenerationIv(
                                DiamondPearl(
                                    "","","","",
                                    "","","", "",
                                ),
                                HeartgoldSoulsilver(
                                    "","","","",
                                    "","","","",
                                ),
                                Platinum(
                                    "","","","",
                                    "","","","",
                                )
                            ),
                            GenerationV(
                                BlackWhite(
                                    Animated(
                                        "","","","",
                                        "","","","",
                                    ),
                                    "","","", "",
                                    "","","", ""
                                )
                            ),
                            GenerationVi(
                                OmegarubyAlphasapphire(
                                    "","","","",
                                ),
                                XY(
                                    "","","","",
                                )
                            ),
                            GenerationVii(
                                Icons("", ""),
                                UltraSunUltraMoon(
                                    "", "", "", ""
                                )
                            ),
                            GenerationViii(
                                IconsX("", "")
                            )
                        )
                    ),
                        listOf(),
                        listOf(),
                        0
                )
            )
        }


    }

    override suspend fun insertFavPokemon(pokemon: PokedexListEntry) {
        favPokemonList.add(pokemon)
        refreshLiveData()
    }

    override fun observeFavPokemons(): LiveData<List<PokedexListEntry>> {
        return observableFavPokemonList
    }

    override suspend fun searchFavPokemons(pokemonName: String): List<PokedexListEntry> {
        return favPokemonList.filter { pokedexListEntry -> pokedexListEntry.pokemonName == pokemonName }
    }


    override suspend fun deleteFavPokemon(pokemon: PokedexListEntry) {
        favPokemonList.remove(pokemon)
        refreshLiveData()
    }
}