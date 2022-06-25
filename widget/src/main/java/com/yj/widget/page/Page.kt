package com.yj.widget.page

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.Lifecycle
import com.yj.widget.*
import kotlinx.android.parcel.Parcelize

/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */

abstract class Page : BaseWidget(), Parcelable {




    val pageAllWidgets = ArrayList<BaseWidget>()
    var rootWidget: Widget? = null
        private set

    private var exitAnimResId = 0
    private lateinit var pageManager: PageManager
    val pageRootView: ViewGroup?
        get() {
            return pageManager.pageRootView
        }


    abstract fun build(): Widget

    internal fun initPage(pageManager: PageManager, pageWidgetWrapper: PageWrapper) {
        initWidget(pageManager.widgetManager, this)
        this.pageManager = pageManager
        this.exitAnimResId = pageWidgetWrapper.exitAnimResId
        rootWidget = build()

    }

    internal fun pageCreate(
        pageRootView: ViewGroup?
    ) {

        val params =
            WidgetCreateParams(
                pageManager.widgetManager,
                rootWidget!!,
                pageRootView,
                this,
                null
            )
        this.currentState = WidgetState.CREATED
        onCreate(pageManager.widgetManager.savedInstanceState)

        rootWidget!!.modifier().index(-1).enableAddView().get().create(params)
        when (pageManager.widgetManager.activity.lifecycle.currentState) {
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
        params: PageWrapper,
        pageRootView: ViewGroup
    ) {
        this.currentState = WidgetState.CREATED
        onCreate(pageManager.widgetManager.savedInstanceState)
        rootWidget!!.modifier().index(-1).enableAddView().get()
            .pageRestore(pageManager.widgetManager, params, pageRootView)
    }

    internal fun restoreConfigurationChange() {
        if (rootWidget!!.contentView?.parent == null) {
            pageManager.widgetManager.activity.setContentView(rootWidget!!.contentView)
        } else {
            (rootWidget!!.contentView?.parent as ViewGroup).removeAllViews()
            pageManager.widgetManager.activity.setContentView(rootWidget!!.contentView)
        }
        when (pageManager.widgetManager.activity.lifecycle.currentState) {
            Lifecycle.State.STARTED -> {
                start()

            }
            Lifecycle.State.RESUMED -> {
                start()
                resume()
            }

        }

    }


    private fun start() {
        if (this.currentState == WidgetState.CREATED ||
            this.currentState == WidgetState.STOP
        ) {
            this.currentState = WidgetState.STARTED
            onStart()
            pageAllWidgets.forEach {
                it.postAction(WidgetAction.ACTION_START)
            }
        }

    }

    private fun resume() {
        if (this.currentState == WidgetState.STARTED ||
            this.currentState == WidgetState.PAUSE
        ) {
            this.currentState = WidgetState.RESUMED
            onResume()
            pageAllWidgets.forEach {
                it.postAction(WidgetAction.ACTION_RESUME)
            }
        }

    }

    private fun pause() {
        if (this.currentState == WidgetState.RESUMED) {
            this.currentState = WidgetState.PAUSE
            onPause()
            pageAllWidgets.forEach {
                it.postAction(WidgetAction.ACTION_PAUESE)
            }
        }

    }


    private fun stop() {
        if (this.currentState == WidgetState.PAUSE) {
            this.currentState = WidgetState.STOP
            onStop()
            pageAllWidgets.forEach {
                it.postAction(WidgetAction.ACTION_STOP)
            }
        }
    }


    private fun destroy() {

        if (this.currentState == WidgetState.STOP) {
            pageAllWidgets.forEach {
                it.postAction(WidgetAction.ACTION_DESTROY_VIEW)
            }
            this.currentState = WidgetState.DESTROYED
            onDestroy()
            pageAllWidgets.forEach {
                it.postAction(WidgetAction.ACTION_DESTROY)
            }
        }

    }

    internal fun activityDestroy() {
        destroy()
        rootWidget!!.parentView?.removeView(rootWidget!!.contentView!!)
        rootWidget!!.parentView = null
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        pageAllWidgets.forEach {
            it.onRestoreInstanceState(savedInstanceState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        pageAllWidgets.forEach {
            it.onSaveInstanceState(outState)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        pageAllWidgets.forEach {
            it.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        pageAllWidgets.forEach {
            it.onConfigurationChanged(newConfig)
        }
    }


    override fun remove() {
        pageManager.removePage(this)
        if (rootWidget == null) {
            return
        }
        rootWidget!!.removeSelf()
        rootWidget = null
    }

    override fun backPressed() {
        pageManager.removePage(this)
        if (rootWidget == null) {
            return
        }

        when (currentState) {
            WidgetState.CREATED -> {
                destroy()

            }
            WidgetState.STARTED -> {
                stop()
                destroy()

            }
            WidgetState.RESUMED -> {
                pause()
                stop()
                destroy()

            }
            WidgetState.PAUSE -> {
                stop()
                destroy()
            }
            WidgetState.STOP -> {
                destroy()
            }
            WidgetState.DESTROYED -> {

            }
            else -> {


            }
        }
        val widget = rootWidget!!
        val contentView = widget.contentView
        rootWidget = null
        if (exitAnimResId > 0) {

            val exitAnim =
                AnimationUtils.loadAnimation(pageManager.widgetManager.activity, exitAnimResId)
            exitAnim?.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                    if (!pageManager.widgetManager.activity.isDestroyed) {
                        widget.removeSelf()
                    }
                }

                override fun onAnimationRepeat(p0: Animation?) {

                }
            })
            contentView?.startAnimation(exitAnim)
        } else {
            widget.removeSelf()
        }


    }


    internal fun pageReCreateView() {

        if (rootWidget!!.contentView != null) {

            if (rootWidget!!.parentView != null) {
                rootWidget!!.parentView?.addView(rootWidget!!.contentView, 0)
            } else {
                pageManager.widgetManager.activity.setContentView(rootWidget!!.contentView)
            }
        }
    }

    internal fun pageBack() {


        when (pageManager.widgetManager.activity.lifecycle.currentState) {
            Lifecycle.State.STARTED -> {
                start()
            }
            Lifecycle.State.RESUMED -> {
                start()
                resume()
            }

        }
    }

    internal fun replaceWidget(newWidget: Widget) {
        rootWidget = newWidget
    }

    internal fun removeView() {
        rootWidget?.parentView?.removeView(rootWidget?.contentView)
    }

    internal fun pageLeave() {


        when (currentState) {

            WidgetState.STARTED -> {
                stop()

            }
            WidgetState.RESUMED -> {
                pause()
                stop()
            }
            WidgetState.PAUSE -> {
                stop()
            }

        }

    }

    override fun postAction(action: WidgetAction) {
        when (action) {


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
            WidgetAction.ACTION_DESTROY -> {
                destroy()
            }

        }

    }


}