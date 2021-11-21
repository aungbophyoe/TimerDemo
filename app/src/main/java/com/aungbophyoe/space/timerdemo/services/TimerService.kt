package com.aungbophyoe.space.timerdemo.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_HIGH
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_LOW
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.aungbophyoe.space.timerdemo.utils.Constants
import com.aungbophyoe.space.timerdemo.MainActivity
import com.aungbophyoe.space.timerdemo.R
import com.aungbophyoe.space.timerdemo.model.TimeEvent
import com.aungbophyoe.space.timerdemo.utils.TimerUtil
import kotlinx.coroutines.*

class TimerService : LifecycleService() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private var isServiceStopped = false
    //Timer properties
    private var lapTime = 0L
    private var timeStarted = 0L
    companion object{
        val timerEvent = MutableLiveData<TimeEvent>()
        val timerInMillis = MutableLiveData<Long>()
    }
    override fun onCreate() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        initValue()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                Constants.START_TIMER -> startForegroundService()
                Constants.STOP_TIMER -> stopService()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initValue(){
        timerEvent.value = TimeEvent.End
        timerInMillis.value = 0L
    }


    private fun startForegroundService(){
        Log.d("Service","start service")
        timerEvent.value =TimeEvent.Start
        startTimer()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel()
        }
        startForeground(Constants.NOTIFICATION_ID,getNotificationBuilder().build())

        timerInMillis.observe(this, {
            if (!isServiceStopped) {
                notificationBuilder.setContentText(
                    TimerUtil.getFormattedTime(it, false)
                )
                notificationManager.notify(Constants.NOTIFICATION_ID, notificationBuilder.build())
            }
        })
    }

    private fun stopService(){
        Log.d("Service","stop service")
        isServiceStopped = true
        initValue()
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
            Constants.NOTIFICATION_ID
        )
        stopForeground(true)
        stopSelf()
    }

    private fun startTimer(){
        try {
            timeStarted = System.currentTimeMillis()
            /*Thread{
                while (timerEvent.value!! == TimeEvent.Start && !isServiceStopped){
                    lapTime = System.currentTimeMillis() - timeStarted
                    timerInMillis.postValue(lapTime)
                    Thread.sleep(50L)
                }
            }.start()*/
            CoroutineScope(Dispatchers.IO).launch {
                while (timerEvent.value!! == TimeEvent.Start && !isServiceStopped){
                    lapTime = System.currentTimeMillis() - timeStarted
                    timerInMillis.postValue(lapTime)
                    delay(50L)
                }
            }
        }catch (e:Exception){
            Log.d("Service","${e.message}")
        }
    }


    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(){
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun getNotificationBuilder():NotificationCompat.Builder {
     notificationBuilder = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
         .setAutoCancel(false)
         .setOngoing(true)
         .setSmallIcon(R.drawable.ic_access_time)
         .setContentTitle("Timer Demo")
         .setContentText("00:00:00")
         .setContentIntent(getMainActivityPendingIntent())

     return notificationBuilder
    }


    private fun getMainActivityPendingIntent() =
        PendingIntent.getActivity(
            this,
            110,
            Intent(this,MainActivity::class.java).apply {
                                                        this.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
}