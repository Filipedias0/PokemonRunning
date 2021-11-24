package com.example.pokedexapp.di

import android.content.Context
import androidx.room.Room
import com.example.pokedexapp.data.remote.PokeApi
import com.example.pokedexapp.db.PokemonDao
import com.example.pokedexapp.db.PokemonDataBase
import com.example.pokedexapp.other.Constants.DATABASE_NAME
import com.example.pokedexapp.repository.DefaultPokemonRepository
import com.example.pokedexapp.repository.PokemonRepository
import com.example.pokedexapp.util.constants.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePokemonDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, PokemonDataBase::class.java, DATABASE_NAME).build()

    @Singleton
    @Provides
    fun providePokemonDao(
        dataBase: PokemonDataBase
    ) = dataBase.pokemonDao()

    @Singleton
    @Provides
    fun provideDefaultPokemonRepository(
        api: PokeApi,
        pokemonDao: PokemonDao
    ) = DefaultPokemonRepository(pokemonDao,api ) as PokemonRepository

    @Singleton
    @Provides
    fun providePokeApi(): PokeApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PokeApi::class.java)
    }
}