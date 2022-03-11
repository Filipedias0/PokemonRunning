package com.example.pokedexapp.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.pokedexapp.data.remote.PokeApi
import com.example.pokedexapp.db.PokemonDao
import com.example.pokedexapp.db.PokemonDataBase
import com.example.pokedexapp.db.RunDAO
import com.example.pokedexapp.repository.DefaultPokemonRepository
import com.example.pokedexapp.repository.DefaultPokemonRunRepository
import com.example.pokedexapp.repository.PokemonRepository
import com.example.pokedexapp.repository.PokemonRunRepository
import com.example.pokedexapp.util.constants.Constants.BASE_URL
import com.example.pokedexapp.util.constants.Constants.DATABASE_NAME
import com.example.pokedexapp.util.constants.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.pokedexapp.util.constants.Constants.KEY_NAME
import com.example.pokedexapp.util.constants.Constants.KEY_WEIGHT
import com.example.pokedexapp.util.constants.Constants.SHARED_PREFERENCES_NAME
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
    fun provideRunDao(
        dataBase: PokemonDataBase
    ) = dataBase.runDao()

    @Singleton
    @Provides
    fun provideDefaultPokemonRepository(
        api: PokeApi,
        pokemonDao: PokemonDao
    ) = DefaultPokemonRepository(pokemonDao,api ) as PokemonRepository

    @Singleton
    @Provides
    fun provideDefaultPokemonRunRepository(
        runDAO: RunDAO
    ) = DefaultPokemonRunRepository(runDAO) as PokemonRunRepository

    @Singleton
    @Provides
    fun providePokeApi(): PokeApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PokeApi::class.java)
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context): SharedPreferences =
        app.getSharedPreferences( SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPref: SharedPreferences) = sharedPref.getString(KEY_NAME, "") ?: ""

    @Singleton
    @Provides
    fun provideWeight(sharedPref: SharedPreferences) = sharedPref.getFloat(KEY_WEIGHT, 80F)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPref: SharedPreferences) =
        sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE, true)

}