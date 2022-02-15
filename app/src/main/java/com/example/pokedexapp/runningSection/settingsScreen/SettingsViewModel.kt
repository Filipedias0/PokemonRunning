package com.example.pokedexapp.runningSection.settingsScreen

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pokedexapp.db.Run
import com.example.pokedexapp.other.SortType
import com.example.pokedexapp.repository.PokemonRunRepository
import com.example.pokedexapp.util.constants.Constants.KEY_NAME
import com.example.pokedexapp.util.constants.Constants.KEY_WEIGHT
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: PokemonRunRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val name = mutableStateOf("")
    val weight = mutableStateOf(0f)
    init {
        name.value = sharedPreferences.getString(KEY_NAME, "").toString()
        weight.value = sharedPreferences.getFloat(KEY_WEIGHT, 80f)
    }

    fun applyChangesToSharedPreferences(name:String, weight: Float, context: Context){

        if(name.isEmpty() || weight.isNaN()){
            Toast.makeText(
                context,
                "Please fill all the fields correctly",
                Toast.LENGTH_SHORT
            ).show()
        }else {
            sharedPreferences.edit()
                .putString(KEY_NAME, name)
                .putFloat(KEY_WEIGHT, weight)
                .apply()

            Toast.makeText(
                context,
                "Update succesfull!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}