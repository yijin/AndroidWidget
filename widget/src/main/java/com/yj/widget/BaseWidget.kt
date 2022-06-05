package com.yj.widget


import android.content.ContentResolver
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.yj.widget.event.WidgetEventManager
import com.yj.widget.event.WidgetEventObserve
import com.yj.widget.page.Page
import com.yj.widget.page.PageStartBuilder

abstract class BaseWidget {


    lateinit var page: Page
        private set
    val TAG = "Widget"
    var currentState: WidgetState = WidgetState.CREATED
        protected set
    val activity: WidgetActivity
        get() {
            return widgetManager.activity
        }


    protected lateinit var widgetManager: WidgetManager
        private set


    val inTopPage: Boolean
        get() = widgetManager.inTopPage(page)


    val childWidgets = ArrayList<BaseWidget>()


    val isResume: Boolean
        get() = currentState == WidgetState.RESUMED

    val isDestroyed: Boolean
        get() = currentState == WidgetState.DESTROYED

    val isActivityDestroyed: Boolean
        get() = activity.isDestroyed


    val contentResolver: ContentResolver?
        get() = if (context == null) {
            null
        } else context!!.contentResolver


    val context: Context
        get() = activity

    fun initWidget(widgetManager: WidgetManager, pageWidget: Page) {
        this.widgetManager = widgetManager
        this.page = pageWidget
    }


    open fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "widget onCreate ${this}")
    }


    open fun onStart() {
        Log.d(TAG, "widget onStart ${this}")
    }


    open fun onResume() {
        Log.d(TAG, "widget onResume ${this}")
    }

    open fun onPause() {
        Log.d(TAG, "widget onPause ${this}")
    }


    open fun onStop() {
        Log.d(TAG, "widget onStop ${this}")
    }


    open fun onDestroyView() {
        Log.d(TAG, "widget onDestroyView ${this}")
    }

    open fun onDestroy() {

        Log.d(TAG, "widget onDestroy ${this}")
    }


    open fun onConfigurationChanged(newConfig: Configuration) {
    }


    open fun backPressed() {

    }

    fun finishActivity() {
        activity.finish()
    }


    fun loadChildDataWidget(widget: DataWidget): Boolean {
        return widgetManager.loadDataWidget(this, widget)
    }


    fun startPage(page: Page): PageStartBuilder {
        return widgetManager.startPage(page)
    }

    fun startPageClearAll(
        page: Page
    ): PageStartBuilder {
        return widgetManager.startPageClearAll(page)
    }

    fun backFirstPage(classType: Class<out Page>): Boolean {
        return widgetManager.backFirstPage(classType)
    }

    fun clearTopPage(): Boolean {
        return widgetManager.backPage(page)
    }

    fun backLastPage(classType: Class<out Page>): Boolean {
        return widgetManager.backLastPage(classType)
    }


    fun startPageSingleTask(
        page: Page
    ): PageStartBuilder {
        return widgetManager.startPageSingleTask(page)
    }

    fun startPageSingleTop(
        page: Page
    ): PageStartBuilder {
        return widgetManager.startPageSingleTop(page)
    }


    protected fun removeAllChild() {
        for (widget in childWidgets) {
            widget.remove()
        }
        childWidgets.clear()
    }


    open fun remove() {
        page?.pageAllWidgets?.remove(this)
        removeAllChild()
    }


    protected val resources: Resources?
        get() = if (context == null) {
            null
        } else context!!.resources

    fun getString(resId: Int): String {
        return if (context == null) {
            ""
        } else context!!.getString(resId)
    }

    fun getColor(resId: Int): Int {
        return if (context == null) {
            0
        } else context!!.resources.getColor(resId)
    }

    fun getDimensionPixelOffset(resId: Int): Int {
        return if (context == null) {
            0
        } else context!!.resources.getDimensionPixelOffset(resId)
    }


    fun getStringArray(id: Int): Array<String>? {
        return if (context == null) {
            null
        } else context!!.resources.getStringArray(id)


    }


    fun getString(resId: Int, vararg formatArgs: Any): String {
        return if (context == null) {
            ""
        } else context!!.getString(resId, *formatArgs)

    }

    fun getDrawable(id: Int): Drawable? {
        return if (context == null) {
            null
        } else context!!.resources.getDrawable(id)
    }


    fun postEvent(event: Any) {
        WidgetEventManager.get(activity).post(event, event.javaClass)
    }

    fun postEvent(key: String, event: Any) {
        WidgetEventManager.get(activity).post(key, event, event.javaClass)
    }

    fun postDelayEvent(event: Any, delay: Long) {
        WidgetEventManager.get(activity).postDelay(event, delay, event.javaClass)
    }

    fun postDelayEvent(key: String, event: Any, delay: Long) {
        WidgetEventManager.get(activity).postDelay(key, event, delay, event.javaClass)
    }

    fun postStickyEvent(event: Any) {
        WidgetEventManager.get(activity).postSticky(event, event.javaClass)
    }

    fun postStickyEvent(key: String, event: Any) {
        WidgetEventManager.get(activity).postSticky(key, event, event.javaClass)
    }


    fun <T> observeEvent(
        tclass: Class<T>,
        observe: WidgetEventObserve<T>
    ) {
        WidgetEventManager.get(activity).observe(tclass, observe)
    }

    fun <T> observeEvent(
        key: String,
        tclass: Class<T>,
        observe: WidgetEventObserve<T>
    ) {
        WidgetEventManager.get(activity).observe(key, tclass, observe)
    }

    fun <T> observeStickyEvent(
        tclass: Class<T>,
        observe: WidgetEventObserve<T>
    ) {
        WidgetEventManager.get(activity).observeSticky(tclass, observe)
    }

    fun <T> observeStickyEvent(
        key: String,
        tclass: Class<T>,
        observe: WidgetEventObserve<T>
    ) {
        WidgetEventManager.get(activity).observeSticky(key, tclass, observe)
    }


    fun runOnUiThread(action: Runnable) {
        activity?.runOnUiThread(action)
    }


    fun post(action: Runnable) {
        handlerMain.post(action)

    }

    fun postDelayed(action: Runnable, delayMillis: Long) {
        handlerMain.postDelayed(action, delayMillis)

    }

    open fun onSaveInstanceState(outState: Bundle) {

    }

    open fun onRestoreInstanceState(savedInstanceState: Bundle) {

    }

    internal abstract fun postAction(action: WidgetAction)


    companion object {
        val handlerMain: Handler = Handler(Looper.getMainLooper())

    }


}
