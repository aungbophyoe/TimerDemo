package com.aungbophyoe.space.timerdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import com.aungbophyoe.space.timerdemo.model.TimeEvent
import com.aungbophyoe.space.timerdemo.services.TimerService
import com.aungbophyoe.space.timerdemo.utils.Constants
import com.aungbophyoe.space.timerdemo.utils.TimerUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var isTimerRunning = false
    private val fab by lazy {
        findViewById<FloatingActionButton>(R.id.fab)
    }
    private val tvTime by lazy {
        findViewById<TextView>(R.id.tvTime)
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
        TimerService.timerEvent.observe(this, {
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

        TimerService.timerInMillis.observe(this,{
            it?.let { value->
                /*GlobalScope.launch {
                    tvTime.text = TimerUtil.getFormattedTime(value,true)
                }*/
                tvTime.text = TimerUtil.getFormattedTime(value,true)
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