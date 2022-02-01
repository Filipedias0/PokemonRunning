package com.example.pokedexapp.di

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.pokedexapp.MainActivity
import com.example.pokedexapp.R
import com.example.pokedexapp.util.constants.Constants.NOTIFICATION_CHANNEL_GENERAL
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext app: Context
    ) = FusedLocationProviderClient(app)

    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(
        @ApplicationContext app: Context
    ) = PendingIntent.getActivity(
        app,
        0,
        Intent(app, MainActivity::class.java),
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    )

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(app, NOTIFICATION_CHANNEL_GENERAL)
        .setTicker(null)
        .setContentTitle("Pokemon app")
        .setContentText("00:00:00")
        .setAutoCancel(false)
        .setOngoing(true)
        .setWhen(System.currentTimeMillis())
        .setSmallIcon(R.drawable.poke_ball_pin)
        .setLargeIcon(BitmapFactory.decodeResource(app.resources,
            R.drawable.poke_ball_pin
            )
        )
        .setColor(Color.parseColor("#f7da64"))
        .setColorized(true)
        .setStyle(androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle())
        .setPriority(Notification.PRIORITY_MAX)
        .setContentIntent(pendingIntent)
}