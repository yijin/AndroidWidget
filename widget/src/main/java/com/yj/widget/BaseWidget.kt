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
import com.yj.widget.page.PageWidgetStartBuilder

abstract class BaseWidget {

    val TAG = "Widget"
    var currentState: WidgetState = WidgetState.CREATED
        protected set
    val activity: WidgetActivity
        get() {
            return widgetManager.activity
        }
    var parentWidget: BaseWidget? = null
        protected set

    protected lateinit var widgetManager: WidgetManager
        private set

    var pageWidget: Widget? = null
        protected set


    val inTopPageWidget: Boolean
        get() = widgetManager.inTopPageWidget(pageWidget)


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

    fun initWidget(widgetManager: WidgetManager) {
        this.widgetManager = widgetManager
    }

    open internal fun create(
        params: WidgetCreateParams
    ) {
        initWidget(params.widgetManager)
        this.pageWidget = params.pageWidget
        this.parentWidget = params.parentWidget
        if (this.parentWidget != null) {
            this.parentWidget?.childWidgets?.add(this)
        }

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

    open fun onDestroy() {

        Log.d(TAG, "widget onDestroy ${this}")
    }


    open fun onConfigurationChanged(newConfig: Configuration) {
    }


    protected fun backPressed() {
        activity.onBackPressed()
    }

    fun finishActivity() {
        activity.finish()
    }


    fun loadChildDataWidget(widget: DataWidget): Boolean {
        return widgetManager.loadDataWidget(this, widget)
    }

    fun loadActivityDataWidget(widget: DataWidget) {
        widgetManager.loadActivityDataWidget(widget)
    }


    fun startPageWidget(widget: Class<out Widget>, bundle: Bundle? = null): PageWidgetStartBuilder {
        return widgetManager.startPageWidget(widget, bundle)
    }

    fun startPageWidgetClearAll(
        widget: Class<out Widget>,
        bundle: Bundle? = null
    ): PageWidgetStartBuilder {
        return widgetManager.startPageWidgetClearAll(widget, bundle)
    }

    fun backFirstWidget(classType: Class<out Widget>): Boolean {
        return widgetManager.backFirstWidget(classType)
    }

    fun backLastWidget(classType: Class<out Widget>): Boolean {
        return widgetManager.backLastWidget(classType)
    }


    fun startPageWidgetSingleTask(
        widget: Class<out Widget>,
        bundle: Bundle? = null
    ): PageWidgetStartBuilder {
        return widgetManager.startPageWidgetSingleTask(widget, bundle)
    }

    fun startPageWidgetSingleTop(
        widget: Class<out Widget>,
        bundle: Bundle? = null
    ): PageWidgetStartBuilder {
        return widgetManager.startPageWidgetSingleTop(widget, bundle)
    }


    protected fun removeAllChild() {
        for (widget in childWidgets) {
            widget.removeSelf()
        }
        childWidgets.clear()
    }


    open fun remove() {

        removeSelf()
    }

    open internal fun removeSelf() {
        parentWidget?.childWidgets?.remove(this)
        pageWidget?.pageAllWidgets?.remove(this)
        widgetManager.remove(this)
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
