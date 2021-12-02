package com.example.pokedexapp.repository

<<<<<<< Updated upstream
=======
import androidx.lifecycle.LiveData
import com.example.pokedexapp.data.models.PokedexListEntry
>>>>>>> Stashed changes
import com.example.pokedexapp.data.remote.responses.*
import com.example.pokedexapp.util.Resource

class FakePokemonRepository : PokemonRepository{
private var shouldReturnNetworkError = false

    override fun setShouldReturnNetworkError(value: Boolean){
        shouldReturnNetworkError = value
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
<<<<<<< Updated upstream
=======


    }

    override suspend fun insertFavPokemon(pokemon: PokedexListEntry) {
        TODO("Not yet implemented")
    }

    override fun observeFavPokemons(): LiveData<List<PokedexListEntry>> {
        TODO("Not yet implemented")
>>>>>>> Stashed changes
    }
}