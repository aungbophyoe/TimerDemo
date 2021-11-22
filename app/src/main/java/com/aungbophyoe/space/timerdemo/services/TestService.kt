package com.aungbophyoe.space.timerdemo.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aungbophyoe.space.timerdemo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TestService : Service() {

    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Test","Service Start")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel()
        }
        startForeground(Constants.NOTIFICATION_ID,notificationBuilder.build())
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
            Constants.NOTIFICATION_ID
        )
        stopForeground(true)
        stopSelf()
        Log.d("Test","Service Stop")
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(){
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManagerCompat.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }
}