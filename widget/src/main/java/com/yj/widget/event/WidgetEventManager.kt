package com.yj.widget.event

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.yj.widget.WidgetActivity


internal class WidgetEventManager private constructor(val activity: WidgetActivity) :
    DefaultLifecycleObserver {

    val stickyEvents: ArrayList<WidgetEventWrapper<*>> = ArrayList()
    val widgetEventObserves: ArrayList<WidgetEventObserveWrapper> = ArrayList()

    companion object {
        fun isMainThread(): Boolean {
            return Looper.myLooper() == Looper.getMainLooper()
        }

        val handler = Handler(Looper.getMainLooper())
        val widgetEventBusCoreData: HashMap<WidgetActivity, WidgetEventManager> =
            HashMap()

        fun get(activity: WidgetActivity): WidgetEventManager {
            var bus = widgetEventBusCoreData.get(activity)
            if (bus != null) {
                return bus
            }
            synchronized(activity)
            {
                bus = widgetEventBusCoreData.get(activity)
                if (bus != null) {
                    return bus!!
                }
                bus = WidgetEventManager(activity)
                widgetEventBusCoreData.put(activity, bus!!)
            }

            return bus!!
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        handler.removeCallbacksAndMessages(null)
        if (activity == owner) {
            activity.lifecycle.removeObserver(this)
            widgetEventBusCoreData.remove(activity)

        }
        stickyEvents.clear()
        widgetEventObserves.clear()
    }


    fun <T> post(
        event: T,
        classType: Class<T>
    ) {

        post("", event, classType)

    }


    fun <T> post(
        key: String,
        event: T,
        classType: Class<T>
    ) {
        if (isMainThread()) {
            postInternal(key, event, classType)
        } else {
            handler.post {
                postInternal(key, event, classType)
            }
        }
    }


    @MainThread
    fun <T> postInternal(
        key: String,
        event: T,
        classType: Class<T>,
    ) {

        for (widgetEvent in widgetEventObserves) {
            if (widgetEvent.classType == classType && key == widgetEvent.key
            ) {
                (widgetEvent.observe as WidgetEventObserve<T>).onChange(event)
            }
        }
    }

    fun <T> postDelay(
        event: T, delay: Long,
        classType: Class<T>
    ) {
        postDelay("", event, delay, classType)
    }

    fun <T> postDelay(key: String, event: T, delay: Long, classType: Class<T>) {
        handler.postDelayed({
            post(key, event, classType)
        }, delay)
    }

    fun <T> postSticky(event: T, classType: Class<T>) {

        postSticky("", event, classType)

    }

    fun <T> postSticky(key: String, event: T, classType: Class<T>) {

        if (isMainThread()) {
            postStickyInternal(key, event, classType)
        } else {
            handler.post {
                postStickyInternal(key, event, classType)
            }
        }

    }

    fun <T> postStickyInternal(key: String, event: T, classType: Class<T>) {

        post(key, event, classType)
        val event = WidgetEventWrapper(key, classType, event)
        if (!stickyEvents.contains(event)) {
            stickyEvents.add(event)
        }
    }

    fun <T> observe(

        classType: Class<T>,
        observe: WidgetEventObserve<T>
    ) {

        observe("", classType, observe)
    }

    fun <T> observe(
        key: String,
        classType: Class<T>,
        observe: WidgetEventObserve<T>
    ) {
        if (isMainThread()) {
            observeInternal(key, classType, observe)
        } else {
            handler.post {
                observeInternal(key, classType, observe)
            }
        }

    }

    fun <T> observeInternal(
        key: String,
        classType: Class<T>,
        observe: WidgetEventObserve<T>
    ) {

        val event = WidgetEventObserveWrapper(key, observe, classType)
        if (!widgetEventObserves.contains(event)) {
            widgetEventObserves.add(event)
        }
    }

    fun <T> observeSticky(

        classType: Class<T>,
        observe: WidgetEventObserve<T>
    ) {
        observeSticky("", classType, observe)
    }

    fun <T> observeSticky(
        key: String,
        classType: Class<T>,
        observe: WidgetEventObserve<T>
    ) {
        if (isMainThread()) {
            observeStickyInternal(key, classType, observe)
        } else {
            handler.post {
                observeStickyInternal(key, classType, observe)
            }
        }
    }

    @MainThread
    fun <T> observeStickyInternal(
        key: String,
        classType: Class<T>,
        observe: WidgetEventObserve<T>
    ) {

        val event = WidgetEventObserveWrapper(key, observe, classType)
        if (!widgetEventObserves.contains(event)) {
            stickyEvents.forEach {
                if (it.key == key && it.classType == classType) {
                    observe.onChange(it.event as T)
                }
            }

            widgetEventObserves.add(event)
        }


    }

    internal class WidgetEventWrapper<T>(val key: String, val classType: Class<*>, val event: T) {

    }


}