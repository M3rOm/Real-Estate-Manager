package com.example.realestatemanager.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.multidex.MultiDexApplication
import com.example.realestatemanager.utils.Constants.NotificationsChannels
//Over 64k methods?
class ApplicationClass : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationsChannels.DEFAULT_CHANNEL_ID,
                NotificationsChannels.DEFAULT_CHANNEL_ID,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = NotificationsChannels.DEFAULT_CHANNEL_ID
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}