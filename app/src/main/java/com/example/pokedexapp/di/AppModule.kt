package com.example.pokedexapp.di

<<<<<<< Updated upstream
=======
import android.app.Application
import android.content.Context
>>>>>>> Stashed changes
import com.example.pokedexapp.data.remote.PokeApi
import com.example.pokedexapp.repository.DefaultPokemonRepository
import com.example.pokedexapp.repository.PokemonRepository
import com.example.pokedexapp.util.constants.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
<<<<<<< Updated upstream
    fun provideDefaultShoppingRepository(
        api: PokeApi
    ) = DefaultPokemonRepository(api) as PokemonRepository

    @Singleton @Provides
=======
    fun providePokemonDatabase(context: Application): PokemonDataBase{
        return PokemonDataBase.getPokemonDataBase(context)
    }

    @Singleton
    @Provides
    fun providePokemonDao(pokemonDB: PokemonDataBase): PokemonDao{
        return pokemonDB.getDAO()
    }

    @Singleton
    @Provides
    fun provideDefaultPokemonRepository(
        api: PokeApi,
        pokemonDao: PokemonDao
    ) = DefaultPokemonRepository(pokemonDao,api ) as PokemonRepository

    @Singleton
    @Provides
>>>>>>> Stashed changes
    fun providePokeApi(): PokeApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PokeApi::class.java)
    }
}