package com.yj.widget


import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yj.widget.event.WidgetEventManager
import com.yj.widget.event.WidgetEventObserve


open class WidgetActivity : ComponentActivity() {
    private lateinit var widgetManager: WidgetManager
        private set


    class WidgetActivityViewModel : ViewModel() {

        var widgetManager: WidgetManager? = null

        override fun onCleared() {
            super.onCleared()
        }

    }

    var hasRestored = false
        private set

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (needSaveWidgets()) {
            val viewModel = ViewModelProvider(this).get(WidgetActivityViewModel::class.java)
            if (viewModel.widgetManager == null) {
                hasRestored = false
                widgetManager = WidgetManager()
                widgetManager.initWidgetManager(this)
                viewModel.widgetManager = widgetManager

            } else {
                hasRestored = true
                widgetManager = viewModel.widgetManager!!
                if (widgetManager.activity != null) {
                    widgetManager.activity.lifecycle.removeObserver(widgetManager)
                }
                widgetManager.initWidgetManager(this)
                // widgetManager.initWidgetManager(this)
            }
        } else {
            widgetManager = WidgetManager()
            widgetManager.initWidgetManager(this)
        }



        if (hasRestored) {


            if (widgetManager.topPageWidget() != null) {


                widgetManager.restore()

            }

        }
    }

    fun needSaveWidgets(): Boolean {
        return true
    }


    fun setPageWidget(widget: Widget) {
        widgetManager.setActivityPage(widget)
    }

    fun loadWidget(parentView: ViewGroup, widget: Widget) {
        widgetManager.loadActivityWidget(parentView, widget)
    }

    fun loadWidget(resId: Int, widget: Widget) {
        widgetManager.loadActivityWidget(findViewById(resId), widget)
    }

    fun loadDataWidget(widget: DataWidget) {
        widgetManager.loadActivityDataWidget(widget)
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        widgetManager!!.onConfigurationChanged(newConfig)
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        widgetManager!!.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        widgetManager!!.onSaveInstanceState(outState)
    }


    fun postEvent(event: Any) {
        WidgetEventManager.get(this).post(event, event.javaClass)
    }

    fun postEvent(key: String, event: Any) {
        WidgetEventManager.get(this).post(key, event, event.javaClass)
    }

    fun postDelayEvent(event: Any, delay: Long) {
        WidgetEventManager.get(this).postDelay(event, delay, event.javaClass)
    }

    fun postDelayEvent(key: String, event: Any, delay: Long) {
        WidgetEventManager.get(this).postDelay(key, event, delay, event.javaClass)
    }

    fun postStickyEvent(event: Any) {
        WidgetEventManager.get(this).postSticky("", event, event.javaClass)
    }

    fun postStickyEvent(key: String, event: Any) {
        WidgetEventManager.get(this).postSticky(key, event, event.javaClass)
    }


    fun <T> observeEvent(
        tclass: Class<T>,
        observe: WidgetEventObserve<T>
    ) {
        WidgetEventManager.get(this).observe(tclass, observe)
    }

    fun <T> observeEvent(
        key: String,
        tclass: Class<T>,
        observe: WidgetEventObserve<T>
    ) {
        WidgetEventManager.get(this).observe(key, tclass, observe)
    }

    fun <T> observeStickyEvent(
        tclass: Class<T>,
        observe: WidgetEventObserve<T>
    ) {
        WidgetEventManager.get(this).observeSticky(tclass, observe)
    }

    fun <T> observeStickyEvent(
        key: String,
        tclass: Class<T>,
        observe: WidgetEventObserve<T>
    ) {
        WidgetEventManager.get(this).observeSticky(key, tclass, observe)
    }

    override fun onBackPressed() {
        if (!widgetManager.onBackPressed()) {
            super.onBackPressed()
        }
    }


}