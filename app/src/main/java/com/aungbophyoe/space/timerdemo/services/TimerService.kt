package com.aungbophyoe.space.timerdemo.services

import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.aungbophyoe.space.timerdemo.Constants
import com.aungbophyoe.space.timerdemo.model.TimeEvent

class TimerService : LifecycleService() {
    companion object{
        val timerEvent = MutableLiveData<TimeEvent>()
    }
    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                Constants.START_TIMER -> {
                    timerEvent.value = TimeEvent.Start
                }
                Constants.STOP_TIMER -> {
                    timerEvent.value = TimeEvent.End
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}