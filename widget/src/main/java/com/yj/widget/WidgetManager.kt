package com.yj.widget


import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.lifecycle.*
import java.util.*
import kotlin.collections.ArrayList


class WidgetManager(val activity: WidgetActivity) : DefaultLifecycleObserver {


    init {
        activity.lifecycle.addObserver(this)
    }


    /**
     * activity 通用的dataWidget
     */
    private val activityDataWidgets: MutableList<DataWidget> = ArrayList()

    /**
     * 不在pageWidget里面的widget
     */
    private val activtyWidgets: MutableList<Widget> = ArrayList()

    /**
     * pageWidget的
     */
    private val pageWidgetManager = PageWidgetManager()
    var pageRootView: ViewGroup? = null
        private set

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        pageWidgetManager.topPageWidget()?.postAction(WidgetAction.ACTION_START)
        activityDataWidgets.forEach {
            it.postAction(WidgetAction.ACTION_START)
        }
        activtyWidgets.forEach {
            it.postAction(WidgetAction.ACTION_START)
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        pageWidgetManager.topPageWidget()?.postAction(WidgetAction.ACTION_RESUME)
        activityDataWidgets.forEach {
            it.postAction(WidgetAction.ACTION_RESUME)
        }
        activtyWidgets.forEach {
            it.postAction(WidgetAction.ACTION_RESUME)
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        pageWidgetManager.topPageWidget()?.postAction(WidgetAction.ACTION_PAUESE)
        activityDataWidgets.forEach {
            it.postAction(WidgetAction.ACTION_PAUESE)
        }
        activtyWidgets.forEach {
            it.postAction(WidgetAction.ACTION_RESUME)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        pageWidgetManager.topPageWidget()?.postAction(WidgetAction.ACTION_STOP)
        activityDataWidgets.forEach {
            it.postAction(WidgetAction.ACTION_STOP)
        }
        activtyWidgets.forEach {
            it.postAction(WidgetAction.ACTION_RESUME)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        pageWidgetManager.topPageWidget()?.postAction(WidgetAction.ACTION_DESTROY)
        activityDataWidgets.forEach {
            it.postAction(WidgetAction.ACTION_DESTROY)
        }
        activtyWidgets.forEach {
            it.postAction(WidgetAction.ACTION_RESUME)
        }
        activityDataWidgets.forEach {
            it.postAction(WidgetAction.ACTION_RESUME)
        }

    }


    fun loadChild(
        parentView: ViewGroup?,
        widget: Widget,
        parentWidget: BaseWidget
    ): Boolean {
        return WidgetLoadBuilder(activity, this, widget).setPageWidget(parentWidget.pageWidget)
            .setParentWidget(parentWidget).setParentView(parentView).load()
    }

    fun loadDataWidget(parentWidget: BaseWidget, widget: DataWidget): Boolean {
        return WidgetLoadBuilder(activity, this, widget).setPageWidget(parentWidget?.pageWidget)
            .setParentWidget(parentWidget).load()

    }

    internal fun loadActivityDataWidget(widget: DataWidget) {
        WidgetLoadBuilder(activity, this, widget).load()
        activityDataWidgets.add(widget)
    }

    internal fun loadActivityWidget(parentView: ViewGroup, widget: Widget) {
        WidgetLoadBuilder(activity, this, widget).setParentView(parentView).load()
        activtyWidgets.add(widget)
    }

    fun replaceWidget(
        oldWidget: Widget,
        newWidget: Widget
    ): Boolean {
        if (oldWidget.isPageWidget) {
            return pageWidgetManager.replaceWidget(oldWidget, newWidget)

        } else {
            var index = -1;
            if (oldWidget.contentView != null && oldWidget.parentView != null) {
                index = oldWidget!!.parentView!!.indexOfChild(oldWidget.contentView)
            }
            val builder = WidgetLoadBuilder(
                activity,
                this,
                newWidget
            ).setPageWidget(if (oldWidget.isPageWidget) newWidget else oldWidget.pageWidget)
                .setParentWidget(oldWidget.parentWidget).setParentView(oldWidget.parentView)
                .setIndex(index)
            oldWidget.removeSelf()

            return builder.load()
        }
    }

    internal fun setActivityPage(widget: Widget) {
        pageWidgetManager.startClearAll(widget).startNoAnim()
    }

    internal fun startPageWidget(
        widget: Widget
    ): PageWidgetStartBuilder {
        return pageWidgetManager.start(widget)
    }


    internal fun startPageWidgetClearAll(
        widget: Widget
    ): PageWidgetStartBuilder {
        return pageWidgetManager.startClearAll(widget)
    }

    fun backFirstWidget(classType: Class<*>): Boolean {
        return pageWidgetManager.backFirstWidget(classType)
    }

    fun backLastWidget(classType: Class<*>): Boolean {
        return pageWidgetManager.backLastWidget(classType)
    }

    internal fun startPageWidgetSingleTask(
        widget: Widget
    ): PageWidgetStartBuilder {
        return pageWidgetManager.startSingleTask(widget)
    }

    internal fun startPageWidgetSingleTop(
        widget: Widget
    ): PageWidgetStartBuilder {
        return pageWidgetManager.startSingleTop(widget)
    }


    fun remove(widget: BaseWidget) {
        activtyWidgets.remove(widget)
        activityDataWidgets.remove(widget)
        pageWidgetManager.widgetList.remove(widget)
    }

    fun containsPageWidget(widget: Widget): Boolean {

        return pageWidgetManager.widgetList.contains(widget)
    }


    fun onConfigurationChanged(newConfig: Configuration) {

        pageWidgetManager.topPageWidget()?.onConfigurationChanged(newConfig)

        activtyWidgets.forEach {
            it.onConfigurationChanged(newConfig)
        }
        activityDataWidgets.forEach {
            it.onConfigurationChanged(newConfig)
        }
    }


    fun onSaveInstanceState(outState: Bundle) {

        pageWidgetManager.topPageWidget()?.onSaveInstanceState(outState)

        activtyWidgets.forEach {
            it.onRestoreInstanceState(outState)
        }
        activityDataWidgets.forEach {
            it.onRestoreInstanceState(outState)
        }
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        pageWidgetManager.topPageWidget()?.onRestoreInstanceState(savedInstanceState)

        activtyWidgets.forEach {
            it.onRestoreInstanceState(savedInstanceState)
        }
        activityDataWidgets.forEach {
            it.onRestoreInstanceState(savedInstanceState)
        }
    }

    fun inTopPageWidget(widget: Widget?): Boolean {
        if (widget == null) {
            return false
        }
        return pageWidgetManager.widgetList.peekLast() == widget
    }

    fun onBackPressed(): Boolean {
        return pageWidgetManager.backPressed()
    }


    internal inner class PageWidgetManager {
        val widgetList = LinkedList<Widget>()

        fun topPageWidget(): Widget? {
            return widgetList.peekLast()
        }

        fun backPressed(): Boolean {
            if (activity.lifecycle.currentState == Lifecycle.State.DESTROYED
            ) {
                return false
            }
            if (widgetList.size < 1) {
                return false
            }
            if (widgetList.size == 1) {
                activity.finish()
                return true
            }
            widgetList.get(widgetList.size - 2).postAction(WidgetAction.ACTION_PAGE_RE_CREATED_VIEW)
            val last = widgetList.peekLast()
            last.removeSelf(false)
            last.postAction(WidgetAction.ACTION_PAGE_BACK)

            return true
        }

        fun backFirstWidget(classType: Class<*>): Boolean {
            if (activity.lifecycle.currentState == Lifecycle.State.DESTROYED
            ) {
                return false
            }
            var index = -1
            for (i in widgetList.indices) {
                if (widgetList.get(i).javaClass == classType) {
                    index = i
                    break
                }
            }
            if (index >= 0) {

                val backCount = widgetList.size - 1 - index
                if (backCount > 0) {
                    index = 1
                    while (index != backCount) {

                        widgetList.get(widgetList.size - 2).removeSelf()
                        index++
                    }

                    backPressed()
                }

                return true
            } else {
                return false
            }
        }

        fun backLastWidget(classType: Class<*>): Boolean {
            if (activity.lifecycle.currentState == Lifecycle.State.DESTROYED
            ) {
                return false
            }

            var index = -1
            for (i in widgetList.indices) {
                if (widgetList.get(i).javaClass == classType) {
                    index = i
                }
            }
            if (index >= 0) {

                val backCount = widgetList.size - 1 - index
                if (backCount > 0) {
                    index = 1
                    while (index != backCount) {
                        widgetList.get(widgetList.size - 2).removeSelf()
                        index++
                    }
                    backPressed()
                }
                return true
            } else {
                return false
            }
        }


        fun replaceWidget(oldWidget: Widget, widget: Widget): Boolean {

            return PageWidgetStartBuilder(activity, this@WidgetManager, widget, this)
                .setParentView(
                    pageRootView
                ).replaceWidget(oldWidget)

        }

        fun start(widget: Widget): PageWidgetStartBuilder {

            return PageWidgetStartBuilder(activity, this@WidgetManager, widget, this).setParentView(
                pageRootView
            )

        }


        fun startClearAll(widget: Widget): PageWidgetStartBuilder {


            return PageWidgetStartBuilder(
                activity, this@WidgetManager, widget, this,
                START_CLEAR_ALL
            ).setParentView(
                pageRootView
            )

        }

        fun startSingleTask(widget: Widget): PageWidgetStartBuilder {


            return PageWidgetStartBuilder(
                activity, this@WidgetManager, widget, this,
                START_SINGLE_TASK
            ).setParentView(
                pageRootView
            )

        }

        fun startSingleTop(widget: Widget): PageWidgetStartBuilder {


            return PageWidgetStartBuilder(
                activity, this@WidgetManager, widget, this,
                START_SINGLE_TOP
            ).setParentView(
                pageRootView
            )
        }


    }

    companion object {
        val START_NORMAL = 0
        val START_CLEAR_ALL = 1
        val START_SINGLE_TOP = 2
        val START_SINGLE_TASK = 3
    }

    inner class PageWidgetStartBuilder internal constructor(
        val activity: WidgetActivity,
        val widgetManager: WidgetManager,
        val widget: Widget,
        val pageWidgetManager: PageWidgetManager,
        val startType: Int = START_NORMAL

    ) {


        val params: WidgetCreateParams =
            WidgetCreateParams(activity, widgetManager, widget)

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
                activity.lifecycle.currentState == Lifecycle.State.DESTROYED
            ) {
                return false
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
                startAnimation = AnimationUtils.loadAnimation(activity, enterAnimResId)
            }
            if (exitAnimResId > 0) {
                params.exitAnim = AnimationUtils.loadAnimation(activity, exitAnimResId)
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
                            if (activity != null && !activity.isDestroyed) {
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
                            if (activity != null && !activity.isDestroyed) {

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
            if (pageRootView == null && widget is Widget && widget.contentView != null) {
                pageRootView = widget.parentView
            }

            return true
        }

        fun replaceWidget(oldWidget: Widget): Boolean {
            if (pageWidgetManager.widgetList.contains(widget) ||
                activity.lifecycle.currentState == Lifecycle.State.DESTROYED
            ) {
                return false
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


    inner class WidgetLoadBuilder internal constructor(
        val activity: WidgetActivity,
        val widgetManager: WidgetManager,
        val widget: BaseWidget,

        ) {

        val params: WidgetCreateParams =
            WidgetCreateParams(activity, widgetManager, widget)


        fun setPageWidget(pageWidget: Widget?): WidgetLoadBuilder {
            params.pageWidget = pageWidget
            return this
        }

        fun setIndex(index: Int): WidgetLoadBuilder {
            params.index = index
            return this
        }

        fun setParentWidget(parentWidget: BaseWidget?): WidgetLoadBuilder {
            params.parentWidget = parentWidget
            return this
        }

        fun setParentView(parentView: ViewGroup?): WidgetLoadBuilder {
            params.parentView = parentView
            return this
        }


        fun load(): Boolean {
            if (activity.lifecycle.currentState == Lifecycle.State.DESTROYED) {
                return false
            }

            widget.create(params)

            return true
        }


    }


}