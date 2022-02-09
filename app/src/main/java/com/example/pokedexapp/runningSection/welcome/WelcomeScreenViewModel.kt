package com.example.pokedexapp.runningSection.welcome

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedexapp.data.models.PokedexListEntry
import com.example.pokedexapp.data.remote.responses.Pokemon
import com.example.pokedexapp.db.Run
import com.example.pokedexapp.repository.DefaultPokemonRepository
import com.example.pokedexapp.repository.PokemonRepository
import com.example.pokedexapp.repository.PokemonRunRepository
import com.example.pokedexapp.runningSection.service.TrackingService
import com.example.pokedexapp.runningSection.service.sendCommandToService
import com.example.pokedexapp.util.Resource
import com.example.pokedexapp.util.constants.Constants
import com.example.pokedexapp.util.constants.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.pokedexapp.util.constants.Constants.KEY_NAME
import com.example.pokedexapp.util.constants.Constants.KEY_WEIGHT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeScreenViewModel @Inject constructor(
    private val sharedPref: SharedPreferences
) : ViewModel() {

    fun writePersonalDataToSharedPref(userName: String, userWeight: Float, context: Context): Boolean {
        val name = userName
        val weight = userWeight

        if(name.isEmpty() || weight == 0F){
            Toast.makeText(
                context,
                "Please make sure all fields are filled in correctly",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight)
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        return true
    }
}