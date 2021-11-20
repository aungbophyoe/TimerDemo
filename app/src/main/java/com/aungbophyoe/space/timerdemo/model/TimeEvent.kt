package com.aungbophyoe.space.timerdemo.model

sealed class TimeEvent{
    object Start : TimeEvent()
    object End : TimeEvent()
}
