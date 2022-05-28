package com.yj.widget


import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.annotation.AnimRes
import androidx.annotation.LayoutRes
import androidx.lifecycle.*
import com.yj.widget.page.PageWidgetWrapper


abstract class Widget : BaseWidget() {
    val layoutInflater: LayoutInflater
        get() = activity.layoutInflater

    val isStopView: Boolean
        get() = currentState == WidgetState.DESTROYED || currentState == WidgetState.STOP_VIEW

    lateinit var contentView: View
        private set

    @AnimRes
    private var exitAnimResId = 0

    val isPageWidget: Boolean
        get() = this == pageWidget


    open protected fun onCreateView(): View {
        return FrameLayout(activity)
    }


    val pageAllWidgets = ArrayList<BaseWidget>()


    val isVisible: Boolean
        get() = contentView != null && contentView!!.visibility == View.VISIBLE


    var parentView: ViewGroup? = null
        get() {
            return if (field != null) field else contentView.parent as ViewGroup?
        }
        internal set


    @SuppressLint("ResourceType")
    override fun create(
        params: WidgetCreateParams
    ) {
        super.create(params)

        params.pageWidget?.pageAllWidgets?.add(this)
        currentState == WidgetState.CREATED

        onCreate(widgetManager.savedInstanceState)
        contentView = onCreateView()
        if (params.parentView != null) {
            this.parentView = params.parentView
            if (params.index >= 0) {
                params.parentView?.addView(contentView, params.index)
            } else {
                params.parentView?.addView(contentView)
            }
        }

        startView()

        when (pageWidget?.currentState) {
            WidgetState.STARTED -> {
                start()
            }
            WidgetState.RESUMED -> {
                start()
                resume()
            }

        }
    }


    internal fun pageCreate(
        widgetManager: WidgetManager,
        params: PageWidgetWrapper,
        pageRootView: ViewGroup?,
        attachToRoot: Boolean = true
    ) {

        initWidget(widgetManager)
        this.exitAnimResId = params.exitAnimResId
        this.parentView = pageRootView
        this.pageWidget = this
        this.currentState == WidgetState.CREATED
        onCreate(widgetManager.savedInstanceState)

        this.contentView = onCreateView()
        if (pageRootView != null) {
            if (attachToRoot) {
                pageRootView.addView(contentView)
            }
        } else {
            activity.setContentView(contentView)
            this.parentView = contentView.parent as ViewGroup?
        }

        startView()

        when (activity.lifecycle.currentState) {
            Lifecycle.State.STARTED -> {
                start()

            }
            Lifecycle.State.RESUMED -> {
                start()
                resume()
            }

        }
    }

    internal fun pageRestore(
        widgetManager: WidgetManager,
        params: PageWidgetWrapper,
        pageRootView: ViewGroup?,
    ) {

        initWidget(widgetManager)
        this.parentView = pageRootView
        this.exitAnimResId = params.exitAnimResId
        this.pageWidget = this
        this.currentState == WidgetState.CREATED
        onCreate(widgetManager.savedInstanceState)
        this.contentView = onCreateView()

    }


    open fun show() {

        if (contentView != null && contentView!!.visibility != View.VISIBLE) {
            contentView!!.visibility = View.VISIBLE
        }

    }

    open fun hide() {
        if (contentView != null && contentView!!.visibility != View.GONE) {
            contentView!!.visibility = View.GONE
        }
    }


    open fun onUserVisibleHint(isVisible: Boolean) {

    }


    fun <T : View> `$`(id: Int): T? {
        return if (contentView == null) {
            null
        } else contentView!!.findViewById(id)

    }

    fun <T : View> findViewById(id: Int): T? {
        return if (contentView == null) {
            null
        } else contentView!!.findViewById(id)

    }


    fun loadChildWidget(widget: Widget): Boolean {
        return loadChildWidget(null, widget, -1)
    }

    fun loadChildWidget(containerView: ViewGroup?, widget: Widget): Boolean {
        return loadChildWidget(containerView, widget, -1)
    }

    fun loadChildWidget(containerView: ViewGroup?, widget: Widget, index: Int = -1): Boolean {
        return widgetManager.loadChild(containerView, widget, this)
    }


    fun replaceWidget(widget: Widget): Boolean {

        val isPage = isPageWidget
        if (isPage) {

        }
        if (widgetManager.replaceWidget(this, widget)) {

            return true
        }
        return false
    }


    open fun onStartView() {
        Log.d(TAG, "widget onStartView ${this}")
    }

    open fun onStopView() {
        Log.d(TAG, "widget onStopView ${this}")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(TAG, "widget onConfigurationChanged ${this}")
        if (isPageWidget) {
            pageAllWidgets.forEach {
                it.onConfigurationChanged(newConfig)
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d(TAG, "widget onRestoreInstanceState ${this}")
        if (isPageWidget) {
            pageAllWidgets.forEach {
                it.onRestoreInstanceState(savedInstanceState)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "widget onSaveInstanceState ${this}")
        if (isPageWidget) {
            pageAllWidgets.forEach {
                it.onSaveInstanceState(outState)
            }
        }
    }

    private fun create() {
        if (currentState != WidgetState.CREATED) {
            currentState = WidgetState.CREATED
            onCreate(widgetManager.savedInstanceState)
        }
        if (isPageWidget) {
            pageAllWidgets.forEach {
                it.postAction(WidgetAction.ACTION_CREATED)
            }
        }

    }

    private fun startView() {
        if (currentState == WidgetState.CREATED || currentState == WidgetState.STOP_VIEW) {
            currentState = WidgetState.STARTED_VIEW
            onStartView()
        }
        if (isPageWidget) {
            pageAllWidgets.forEach {
                it.postAction(WidgetAction.ACTION_CREATED_VIEW)
            }
        }

    }

    private fun start() {
        if (currentState == WidgetState.STARTED_VIEW || currentState == WidgetState.STOP) {
            currentState = WidgetState.STARTED

            onStart()
        }
    }

    private fun resume() {

        if (currentState == WidgetState.STARTED || currentState == WidgetState.PAUSE) {
            currentState = WidgetState.RESUMED
            onResume()
        }
        if (isPageWidget) {
            pageAllWidgets.forEach {

                it.postAction(WidgetAction.ACTION_RESUME)
            }
        }
    }

    private fun pause() {
        if (currentState == WidgetState.RESUMED) {
            currentState = WidgetState.PAUSE

            onPause()
        }
        if (isPageWidget) {
            pageAllWidgets.forEach {
                it.postAction(WidgetAction.ACTION_PAUESE)
            }
        }
    }

    private fun stop() {

        if (currentState == WidgetState.PAUSE || currentState == WidgetState.STARTED) {
            currentState = WidgetState.STOP
            onStop()
        }
        if (isPageWidget) {
            pageAllWidgets.forEach {
                it.postAction(WidgetAction.ACTION_STOP)
            }
        }
    }


    private fun stopView() {

        if (currentState == WidgetState.STOP || currentState == WidgetState.STARTED_VIEW) {
            currentState = WidgetState.STOP_VIEW
            onStopView()
        }
        if (isPageWidget) {
            pageAllWidgets.forEach {
                it.postAction(WidgetAction.ACTION_DESTROY_VIEW)
            }
        }

    }


    private fun destroy() {

        //destroyView()
        if (currentState == WidgetState.STOP_VIEW) {
            currentState = WidgetState.DESTROYED
            onDestroy()
        }


        if (isPageWidget) {

            pageAllWidgets.forEach {
                it.postAction(WidgetAction.ACTION_DESTROY)
            }
        }


    }


    private fun pageWidgetReCreateView() {
        if (!isPageWidget) {
            return
        }

        if (currentState == WidgetState.STOP_VIEW || currentState == WidgetState.CREATED) {

            if (contentView != null) {

                if (parentView != null) {
                    parentView?.addView(contentView, 0)
                } else {
                    activity.setContentView(contentView)
                }
            }
            startView()

        }
    }

    private fun pageWidgetBack() {
        if (!isPageWidget) {
            return
        }

        when (activity.lifecycle.currentState) {
            Lifecycle.State.STARTED -> {
                start()
            }
            Lifecycle.State.RESUMED -> {
                start()
                resume()
            }

        }
    }

    private fun pageWidgetLeave() {

        if (!isPageWidget) {
            return
        }

        when (currentState) {
            WidgetState.STARTED_VIEW -> {
                stopView()

            }
            WidgetState.STARTED -> {
                stop()
                stopView()

            }
            WidgetState.RESUMED -> {
                pause()
                stop()
                stopView()
            }
            WidgetState.PAUSE -> {

                stop()
                stopView()
            }
            WidgetState.STOP -> {
                stopView()
            }
        }

    }

    override fun postAction(action: WidgetAction) {

        when (action) {
            WidgetAction.ACTION_CREATED -> {
                create()
            }
            WidgetAction.ACTION_CREATED_VIEW -> {
                startView()
            }
            WidgetAction.ACTION_START -> {
                start()
            }
            WidgetAction.ACTION_RESUME -> {
                resume()
            }
            WidgetAction.ACTION_PAUESE -> {
                pause()
            }
            WidgetAction.ACTION_STOP -> {
                stop()
            }
            WidgetAction.ACTION_DESTROY_VIEW -> {
                stopView()
            }
            WidgetAction.ACTION_PAGE_LEAVE -> {
                pageWidgetLeave()
            }
            WidgetAction.ACTION_PAGE_BACK -> {
                pageWidgetBack()
            }
            WidgetAction.ACTION_DESTROY -> {
                destroy()
            }
            WidgetAction.ACTION_ACTIVITY_DESTROY -> {
                destroy()
                parentView?.removeView(contentView)
                parentView = null
            }
            WidgetAction.ACTION_PAGE_RE_CREATED_VIEW -> {
                pageWidgetReCreateView()
            }
        }


    }

    private var isRemove = false
    override fun remove() {
        if ((isPageWidget && !widgetManager.containsPageWidget(this)) || isRemove) {
            return

        }
        if (isPageWidget && inTopPageWidget) {
            widgetManager.onBackPressed()
        } else {
            removeSelf()
        }
    }

    @SuppressLint("ResourceType")
    internal fun removeSelf(noAnim: Boolean) {
        if (isRemove) {
            return
        }
        isRemove = true
        super.removeSelf()
        when (currentState) {
            WidgetState.STARTED_VIEW -> {
                stopView()
                destroy()

            }
            WidgetState.STARTED -> {
                stop()
                stopView()
                destroy()

            }
            WidgetState.RESUMED -> {
                pause()
                stop()
                stopView()
                destroy()

            }
            WidgetState.PAUSE -> {
                stop()
                stopView()
                destroy()
            }
            WidgetState.STOP -> {
                stopView()
                destroy()
            }
            else -> {
                destroy()

            }
        }
        if (exitAnimResId > 0 && !noAnim) {

            val exitAnim = AnimationUtils.loadAnimation(activity, exitAnimResId)
            exitAnim?.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                    if (!isActivityDestroyed) {
                        parentView?.removeView(contentView)
                    }
                }

                override fun onAnimationRepeat(p0: Animation?) {

                }
            })
            contentView.startAnimation(exitAnim)
        } else {
            parentView?.removeView(contentView)
        }
    }

    override fun removeSelf() {

        removeSelf(true)
    }
}



