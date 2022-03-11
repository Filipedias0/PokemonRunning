package com.example.pokedexapp.runningSection.welcome

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.pokedexapp.util.constants.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.pokedexapp.util.constants.Constants.KEY_NAME
import com.example.pokedexapp.util.constants.Constants.KEY_WEIGHT
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeScreenViewModel @Inject constructor(
    private val sharedPref: SharedPreferences
) : ViewModel() {
    fun writePersonalDataToSharedPref(userName: String, userWeight: Float, context: Context): Boolean {

        if(userName.isEmpty() || userWeight == 0F){
            Toast.makeText(
                context,
                "Please make sure all fields are filled in correctly",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME, userName)
            .putFloat(KEY_WEIGHT, userWeight)
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        return true
    }
}