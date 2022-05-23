package com.yj.widget.event

interface WidgetEventObserve<T> {
    fun onChange(event: T)

}