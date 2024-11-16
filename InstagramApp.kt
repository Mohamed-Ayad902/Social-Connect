package com.mayad.instagram.di

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class InstagramApp : Application() {

    companion object {
        const val CHANNEL_ID = "Instagram Channel Id"
    }

    override fun onCreate() {
        createNotificationChannel()
        super.onCreate()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Counter",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Used for general notifications"

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}