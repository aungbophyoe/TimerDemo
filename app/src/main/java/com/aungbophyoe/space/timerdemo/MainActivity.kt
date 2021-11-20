package com.aungbophyoe.space.timerdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.aungbophyoe.space.timerdemo.model.TimeEvent
import com.aungbophyoe.space.timerdemo.services.TimerService
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private var isTimerRunning = false
    private val fab by lazy {
        findViewById<FloatingActionButton>(R.id.fab)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fab.setOnClickListener{
            toggleTimer()
        }
        timerObserver()
    }

    private fun toggleTimer(){
        if(isTimerRunning){
            sendCommandToService(Constants.STOP_TIMER)
        }else{
            sendCommandToService(Constants.START_TIMER)
        }
    }

    private fun timerObserver(){
        TimerService.timerEvent.observe(this, Observer {
            it?.let { timeEvent ->
                when(timeEvent){
                    is TimeEvent.Start -> {
                        isTimerRunning = true
                        fab.setImageResource(R.drawable.ic_stop)
                    }
                    is TimeEvent.End -> {
                        isTimerRunning = false
                        fab.setImageResource(R.drawable.ic_timer)
                    }
                }
            }
        })
    }

    private fun sendCommandToService(action:String){
        startService(
            Intent(this,TimerService::class.java).apply {
                this.action = action
            }
        )
    }
}