package com.yj.widget.page

import android.util.Log
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.lifecycle.Lifecycle
import com.yj.widget.*
import com.yj.widget.page.PageWidgetManager.Companion.START_CLEAR_ALL
import com.yj.widget.page.PageWidgetManager.Companion.START_NORMAL
import com.yj.widget.page.PageWidgetManager.Companion.START_SINGLE_TASK
import com.yj.widget.page.PageWidgetManager.Companion.START_SINGLE_TOP

class PageWidgetStartBuilder internal constructor(
    val widgetManager: WidgetManager,
    val widget: Widget,
    val pageWidgetManager: PageWidgetManager,
    val startType: Int = START_NORMAL

) {

    var pageRootView: ViewGroup? = null
        private set

    val params: WidgetCreateParams =
        WidgetCreateParams(widgetManager, widget)

    init {
        params.pageWidget = widget
    }


    fun setAttachToRoot(attachToRoot: Boolean): PageWidgetStartBuilder {
        params.attachToRoot = attachToRoot
        return this
    }


    fun setParentView(parentView: ViewGroup?): PageWidgetStartBuilder {
        params.parentView = parentView
        return this
    }


    fun start(@AnimRes enterAnimResId: Int, @AnimRes exitAnimResId: Int): Boolean {
        if (pageWidgetManager.widgetList.contains(widget) ||
            widgetManager.activity.lifecycle.currentState == Lifecycle.State.DESTROYED
        ) {
            return false
        }
        if (params.parentView == null) {
            params.parentView = pageRootView
        }

        val removeList = ArrayList<Widget>()

        when (startType) {
            START_CLEAR_ALL -> {
                var index = 0
                while (index < pageWidgetManager.widgetList.size) {
                    removeList.add(pageWidgetManager.widgetList.get(index))
                    index++
                }
            }
            START_SINGLE_TOP -> {
                var index = pageWidgetManager.widgetList.size - 1
                while (index >= 0 && pageWidgetManager.widgetList.get(index).javaClass == widget::javaClass
                ) {
                    removeList.add(pageWidgetManager.widgetList.get(index))
                    index--
                }
            }
            START_SINGLE_TASK -> {

                var index = -1
                for (i in pageWidgetManager.widgetList.indices) {
                    if (pageWidgetManager.widgetList.get(i).javaClass == widget.javaClass
                    ) {
                        index = i
                        break
                    }
                }
                if (index >= 0) {

                    while (index < pageWidgetManager.widgetList.size) {
                        removeList.add(pageWidgetManager.widgetList.get(index))
                        index++
                    }
                }
            }

        }
        removeList.forEach {
            pageWidgetManager.widgetList.remove(it)
        }
        params.exitAnim = null


        var startAnimation: Animation? = null
        if (enterAnimResId > 0) {
            startAnimation = AnimationUtils.loadAnimation(widgetManager.activity, enterAnimResId)
        }
        if (exitAnimResId > 0) {
            params.exitAnim = AnimationUtils.loadAnimation(widgetManager.activity, exitAnimResId)
        }

        widget.create(params)

        val lastWidget = pageWidgetManager.widgetList.peekLast()
        if (widget.contentView != null && widget.parentView != null && startAnimation != null) {
            if (lastWidget != null) {
                lastWidget.postAction(WidgetAction.ACTION_PAGE_LEAVE)

                startAnimation?.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {

                    }

                    override fun onAnimationEnd(p0: Animation?) {
                        if (widgetManager.activity != null && !widgetManager.activity.isDestroyed) {
                            lastWidget.parentView?.removeView(lastWidget.contentView)
                            //startEnd()
                            Log.d("yijin", "sb ${removeList.size}")
                            removeList.forEach {
                                it.removeSelf()
                            }

                        }

                    }

                    override fun onAnimationRepeat(p0: Animation?) {

                    }
                })
                widget.contentView.startAnimation(startAnimation)
            } else {
                startAnimation?.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {

                    }

                    override fun onAnimationEnd(p0: Animation?) {
                        if (widgetManager.activity != null && !widgetManager.activity.isDestroyed) {

                            Log.d("yijin", "sb ${removeList.size}")
                            removeList.forEach {
                                it.removeSelf()
                            }

                        }
                    }

                    override fun onAnimationRepeat(p0: Animation?) {

                    }
                })
                widget.contentView.startAnimation(startAnimation)

            }

        } else {
            if (lastWidget != null) {
                lastWidget.postAction(WidgetAction.ACTION_PAGE_LEAVE)
                lastWidget.parentView?.removeView(lastWidget.contentView)

                // startEnd()
            }
            removeList.forEach {
                it.removeSelf()
            }
        }
        pageWidgetManager.widgetList.add(widget)
        pageRootView = widget.parentView

        return true
    }

    fun replaceWidget(oldWidget: Widget): Boolean {
        if (pageWidgetManager.widgetList.contains(widget) ||
            widgetManager.activity.lifecycle.currentState == Lifecycle.State.DESTROYED
        ) {
            return false
        }

        if (params.parentView == null) {
            params.parentView = pageRootView
        }

        params.pageWidget = params.widget as Widget
        if (oldWidget.inTopPageWidget) {
            widget.create(params)
            pageWidgetManager.widgetList.add(widget)
        } else {
            val index = pageWidgetManager.widgetList.indexOf(oldWidget)
            params.attachToRoot = false
            widget.create(params)
            pageWidgetManager.widgetList.add(index, widget)
        }
        oldWidget.removeSelf()

        return true
    }

    fun start(): Boolean {
        if (params.pageWidget == widget && pageWidgetManager.widgetList.size > 0) {
            return startRightToLeft()
        } else {
            return startNoAnim()
        }
    }

    fun startNoAnim(): Boolean {
        return start(0, 0)
    }


    fun startBottomToTop(): Boolean {
        return start(
            R.anim.widget_bottom_to_top_enter,
            R.anim.widget_bottom_to_top_exit
        )
    }

    fun startTopToBottom(): Boolean {
        return start(
            R.anim.widget_top_to_bottom_enter,
            R.anim.widget_top_to_bottom_exit
        )
    }

    fun startLeftToRight(): Boolean {
        return start(
            R.anim.widget_left_to_right_enter,
            R.anim.widget_left_to_right_exit
        )
    }

    fun startRightToLeft(): Boolean {
        return start(
            R.anim.widget_right_to_left_enter,
            R.anim.widget_right_to_left_exit
        )
    }


}
