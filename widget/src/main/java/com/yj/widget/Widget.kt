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
import androidx.annotation.LayoutRes
import androidx.lifecycle.*
import java.lang.RuntimeException


abstract class Widget : BaseWidget() {
    val layoutInflater: LayoutInflater
        get() = activity.layoutInflater

    val isDestoryView: Boolean
        get() = currentState == WidgetState.DESTROYED || currentState == WidgetState.DESTROY_VIEW

    lateinit var contentView: View
        private set

    var exitAnim: Animation? = null
        private set

    val isPageWidget: Boolean
        get() = this == pageWidget


    @get:LayoutRes
    open protected val layoutId: Int = 0

    open protected fun onLoadContentView(): View {
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

        if (!isPageWidget) {
            params.pageWidget?.pageAllWidgets?.add(this)
        }
        this.exitAnim = params.exitAnim
        currentState == WidgetState.CREATED

        onCreate()
        if (layoutId > 0) {
            layoutInflater.inflate(layoutId, params.parentView, false)
        } else {
            contentView = onLoadContentView()
        }
        if (params.parentView != null) {
            this.parentView = params.parentView
            if (params.index >= 0) {
                params.parentView?.addView(contentView, params.index)
            } else if (params.attachToRoot) {
                params.parentView?.addView(contentView)
            }

        } else if (isPageWidget) {
            activity.setContentView(contentView)
            this.parentView = contentView.parent as ViewGroup?
        }

        createView()

        if (isPageWidget) {
            when (activity.lifecycle.currentState) {
                Lifecycle.State.STARTED -> {
                    start()

                }
                Lifecycle.State.RESUMED -> {
                    start()
                    resume()
                }

            }
        } else {
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
        val anim = exitAnim
        val isPage = isPageWidget
        if (widgetManager.replaceWidget(this, widget)) {
            if (isPage && anim != null) {
                widget.exitAnim = anim
            }
            return true
        }
        return false
    }


    open fun onCreatedView() {
        Log.d(TAG, "widget onCreatedView ${this}")
    }

    open fun onDestroyView() {
        Log.d(TAG, "widget onDestroyView ${this}")
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
            onCreate()
        }
        if (isPageWidget) {
            pageAllWidgets.forEach {
                it.postAction(WidgetAction.ACTION_CREATED)
            }
        }

    }

    private fun createView() {
        if (currentState == WidgetState.CREATED || currentState == WidgetState.DESTROY_VIEW) {
            currentState = WidgetState.CREATED_VIEW
            onCreatedView()
        }
        if (isPageWidget) {
            pageAllWidgets.forEach {
                it.postAction(WidgetAction.ACTION_CREATED_VIEW)
            }
        }

    }

    private fun start() {
        if (currentState == WidgetState.CREATED_VIEW || currentState == WidgetState.STOP) {
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


    private fun destroyView() {

        if (currentState == WidgetState.STOP || currentState == WidgetState.CREATED_VIEW) {
            currentState = WidgetState.DESTROY_VIEW
            onDestroyView()
        }
        if (isPageWidget) {
            pageAllWidgets.forEach {
                it.postAction(WidgetAction.ACTION_DESTROY_VIEW)
            }
        }

    }


    private fun destroy() {

        //destroyView()
        if (currentState == WidgetState.DESTROY_VIEW) {
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

        if (currentState == WidgetState.DESTROY_VIEW || currentState == WidgetState.CREATED) {

            if (contentView != null) {

                if (parentView != null) {
                    parentView?.addView(contentView, 0)
                } else {
                    activity.setContentView(contentView)
                }
            }
            createView()

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
            WidgetState.CREATED_VIEW -> {
                destroyView()

            }
            WidgetState.STARTED -> {
                stop()
                destroyView()

            }
            WidgetState.RESUMED -> {
                pause()
                stop()
                destroyView()
            }
            WidgetState.PAUSE -> {

                stop()
                destroyView()
            }
            WidgetState.STOP -> {
                destroyView()
            }
        }

    }

    override fun postAction(action: WidgetAction) {

        when (action) {
            WidgetAction.ACTION_CREATED -> {
                create()
            }
            WidgetAction.ACTION_CREATED_VIEW -> {
                createView()
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
                destroyView()
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

    internal fun removeSelf(noAnim: Boolean) {
        if (isRemove) {
            return
        }
        isRemove = true
        super.removeSelf()
        when (currentState) {
            WidgetState.CREATED_VIEW -> {
                destroyView()
                destroy()

            }
            WidgetState.STARTED -> {
                stop()
                destroyView()
                destroy()

            }
            WidgetState.RESUMED -> {
                pause()
                stop()
                destroyView()
                destroy()

            }
            WidgetState.PAUSE -> {
                stop()
                destroyView()
                destroy()
            }
            WidgetState.STOP -> {
                destroyView()
                destroy()
            }
            else -> {
                destroy()

            }
        }
        if (exitAnim != null && !noAnim) {

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



