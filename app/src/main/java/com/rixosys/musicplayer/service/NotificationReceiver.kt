package com.rixosys.musicplayer.service


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val serviceIntent = Intent(it, MusicService::class.java)
            intent?.action?.let { action ->

                serviceIntent.action = action
                ContextCompat.startForegroundService(it, serviceIntent)
            }
        }
    }
}