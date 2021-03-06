package com.example.pokedexapp.util.constants

import android.graphics.Color

object Constants {

    const val BASE_URL = "https://pokeapi.co/api/v2/"

    const val PAGE_SIZE = 20

    const val DATABASE_NAME = "pokemon_db"

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"

    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"

    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"

    const val LOCATION_UPDATE_INTERVAL = 5000L

    const val FASTEST_LOCATION_INTERVAL = 2000L

    const val POLYLINE_WIDTH = 8f

    const val MAP_ZOOM = 15f

    const val TIMER_UPDATE_INTERVAL = 50L

    const val NOTIFICATION_CHANNEL_GENERAL = "Checking"

    const val NOTIFICATION_ID = 1

    const val POLYLINE_COLOR = Color.RED

    const val SHARED_PREFERENCES_NAME = "sharedPref"

    const val KEY_FIRST_TIME_TOGGLE = "KEY_FIRST_TIME_TOGGLE"

    const val KEY_NAME = "KEY_NAME"

    const val KEY_WEIGHT = "KEY_WEIGHT"

    const val INTENT_COMMAND = "Command"

    const val INTENT_COMMAND_REPLY = "Reply"

    const val CODE_FOREGROUND_SERVICE = 1
}