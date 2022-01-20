package com.example.pokedexapp.runningSection.service

import android.content.Context
import android.content.Intent
import android.os.Build

fun foregroundStartService(command: String, context: Context) {
    val intent = Intent(context, MakeItService::class.java)
    if (command == "Start") {
        intent.putExtra(INTENT_COMMAND, command)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    } else if (command == "Exit") {
        intent.putExtra(INTENT_COMMAND, command)

        context.stopService(intent)
    }
}