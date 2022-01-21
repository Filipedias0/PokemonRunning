package com.example.pokedexapp.runningSection.service

import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.pokedexapp.util.constants.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.pokedexapp.util.constants.Constants.ACTION_STOP_SERVICE

fun foregroundStartService(command: String, context: Context) {
    val intent = Intent(context, MakeItService::class.java)
    if (command == ACTION_START_OR_RESUME_SERVICE) {
        intent.putExtra(INTENT_COMMAND, command)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }else if (command == ACTION_STOP_SERVICE) {
        intent.putExtra(INTENT_COMMAND, command)

        context.stopService(intent)
    }
}