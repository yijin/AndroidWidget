package com.yj.widget.page


import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.Lifecycle
import com.yj.widget.*
import com.yj.widget.WidgetAction
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

internal class PageWidgetManager(val widgetManager: WidgetManager) {
    private val widgetData = HashMap<PageWidgetWrapper, Widget>()
    private val pageData = LinkedList<PageWidgetWrapper>()

    private var pageRootView: ViewGroup? = null
        private set


    fun onSaveInstanceState(outState: Bundle) {

        val list = ArrayList<PageWidgetWrapper>()
        pageData.forEach {
            list.add(it)
        }
        outState.putParcelableArrayList(PAGE_SAVAE_KAEY, list)
        pageData.forEach {
            getPageWidget(it).onSaveInstanceState(outState)
        }
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        pageData.forEach {
            getPageWidget(it).onRestoreInstanceState(savedInstanceState)
        }
    }

    fun topPageWidget(): Widget? {
        if (pageData.peekLast() == null) {
            return null
        }
        return getPageWidget(pageData.peekLast())
    }

    fun pageSize(): Int {
        return pageData.size
    }

    fun destroy() {
        pageData.forEach {
            getPageWidget(it).postAction(WidgetAction.ACTION_DESTROY_VIEW)
            getPageWidget(it).postAction(WidgetAction.ACTION_ACTIVITY_DESTROY)
        }

    }

    private fun clear() {
        destroy()
        pageData.clear()
        widgetData.clear()
    }

    fun restoreNoConfigurationChange(savedInstanceState: Bundle) {
        clear()
        val list = savedInstanceState.getParcelableArrayList<PageWidgetWrapper>(PAGE_SAVAE_KAEY)
        if (list.isNullOrEmpty()) {
            return
        }
        list.forEach {
            pageData.add(it)
        }

        val topPage = pageData.peekLast()
        val topWidget = topPage.widgetType.newInstance()
        topWidget.pageCreate(widgetManager, topPage, null)
        widgetData.put(topPage, topWidget)
        pageRootView = topWidget.parentView
        pageData.forEach {
            if (it == topPage) {

            } else {
                val widget = it.widgetType.newInstance()
                widget.pageRestore(widgetManager, it, pageRootView)
                widgetData.put(it, widget)
            }
        }

    }

    fun restoreConfigurationChange() {
        if (topPageWidget() == null) {
            return
        }

        if (topPageWidget()!!.contentView?.parent == null) {
            widgetManager.activity.setContentView(topPageWidget()!!.contentView)
        } else {
            (topPageWidget()!!.contentView?.parent as ViewGroup).removeAllViews()
            widgetManager.activity.setContentView(topPageWidget()!!.contentView)
        }
        pageRootView = topPageWidget()!!.parentView
        when (widgetManager.activity.lifecycle.currentState) {
            Lifecycle.State.STARTED -> {
                topPageWidget()!!.postAction(WidgetAction.ACTION_START)

            }
            Lifecycle.State.RESUMED -> {
                topPageWidget()!!.postAction(WidgetAction.ACTION_START)
                topPageWidget()!!.postAction(WidgetAction.ACTION_RESUME)
            }

        }


    }

    private fun getPageWidget(page: PageWidgetWrapper): Widget {
        var widget = widgetData.get(page)
        return widget!!

    }

    fun backPressed(): Boolean {
        if (widgetManager.activity.lifecycle.currentState == Lifecycle.State.DESTROYED
        ) {
            return false
        }
        if (pageData.size < 1) {
            return false
        }
        if (pageData.size == 1) {
            widgetManager.activity.finish()
            return true
        }
        var preWidget: Widget = getPageWidget(pageData.get(pageData.size - 2))

        preWidget.postAction(WidgetAction.ACTION_PAGE_RE_CREATED_VIEW)
        val last = topPageWidget()
        last?.removeSelf(false)
        preWidget.postAction(WidgetAction.ACTION_PAGE_BACK)

        return true
    }


    fun removePage(widget: Widget) {
        widgetData.forEach {
            if (it.value == widget) {
                widgetData.remove(it.key)
                pageData.remove(it.key)
                return
            }
        }

    }

    private fun removePage(page: PageWidgetWrapper) {
        widgetData.remove(page)
        pageData.remove(page)
    }

    private fun addPage(widget: Widget, page: PageWidgetWrapper) {
        pageData.add(page)
        widgetData.put(page, widget)
    }


    fun containsPageWidget(widget: Widget): Boolean {
        widgetData.forEach {
            if (it.value == widget) {

                return true
            }
        }
        return false

    }


    fun backFirstWidget(classType: Class<out Widget>): Boolean {
        if (widgetManager.activity.lifecycle.currentState == Lifecycle.State.DESTROYED
        ) {
            return false
        }
        var index = -1
        for (i in pageData.indices) {
            if (pageData.get(i).widgetType == classType) {
                index = i
                break
            }
        }
        if (index >= 0) {

            val backCount = pageData.size - 1 - index
            if (backCount > 0) {
                index = 1
                while (index != backCount) {

                    getPageWidget(pageData.get(pageData.size - 2))?.removeSelf()
                    index++
                }

                backPressed()
            }

            return true
        } else {
            return false
        }
    }

    fun backLastWidget(classType: Class<out Widget>): Boolean {
        if (widgetManager.activity.lifecycle.currentState == Lifecycle.State.DESTROYED
        ) {
            return false
        }

        var index = -1
        for (i in pageData.indices) {
            if (pageData.get(i).widgetType == classType) {
                index = i
            }
        }
        if (index >= 0) {

            val backCount = pageData.size - 1 - index
            if (backCount > 0) {
                index = 1
                while (index != backCount) {
                    getPageWidget(pageData.get(pageData.size - 2))?.removeSelf()
                    index++
                }
                backPressed()
            }
            return true
        } else {
            return false
        }
    }


    fun start(classType: Class<out Widget>, bundle: Bundle?): PageWidgetStartBuilder {

        return PageWidgetStartBuilder(
            widgetManager,
            PageWidgetWrapper(classType, bundle),
            this
        )

    }


    fun startClearAll(classType: Class<out Widget>, bundle: Bundle?): PageWidgetStartBuilder {


        return PageWidgetStartBuilder(
            widgetManager, PageWidgetWrapper(classType, bundle), this,
            START_CLEAR_ALL
        )

    }

    fun startSingleTask(classType: Class<out Widget>, bundle: Bundle?): PageWidgetStartBuilder {


        return PageWidgetStartBuilder(
            widgetManager, PageWidgetWrapper(classType, bundle), this,
            START_SINGLE_TASK
        )

    }

    fun startSingleTop(classType: Class<out Widget>, bundle: Bundle?): PageWidgetStartBuilder {

        return PageWidgetStartBuilder(
            widgetManager, PageWidgetWrapper(classType, bundle), this,
            START_SINGLE_TOP
        )
    }

    internal fun start(page: PageWidgetWrapper, params: PageWidgetCreateParams) {
        val removeList = ArrayList<PageWidgetWrapper>()

        when (params.startType) {
            START_CLEAR_ALL -> {
                var index = 0
                while (index < pageData.size) {
                    removeList.add(pageData.get(index))
                    index++
                }
            }
            START_SINGLE_TOP -> {
                var index = pageData.size - 1
                while (index >= 0 && pageData.get(index).widgetType == page.widgetType
                ) {
                    removeList.add(pageData.get(index))
                    index--
                }
            }
            START_SINGLE_TASK -> {

                var index = -1
                for (i in pageData.indices) {
                    if (pageData.get(i).widgetType == page.widgetType
                    ) {
                        index = i
                        break
                    }
                }
                if (index >= 0) {

                    while (index < pageData.size) {
                        removeList.add(pageData.get(index))
                        index++
                    }
                }
            }

        }
        removeList.forEach {
            pageData.remove(it)
        }

        val lastWidget = topPageWidget()
        val widget = page.widgetType.newInstance()
        widget.pageCreate(widgetManager, page, pageRootView)

        val enterAnim = if (params.enterAnimResId > 0)
            AnimationUtils.loadAnimation(widgetManager.activity, params.enterAnimResId) else null
        if (widget.contentView != null && widget.parentView != null && enterAnim != null) {

            if (lastWidget != null) {
                lastWidget.postAction(WidgetAction.ACTION_PAGE_LEAVE)

                enterAnim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {

                    }

                    override fun onAnimationEnd(p0: Animation?) {
                        enterAnim.cancel()
                        if (widgetManager.activity != null && !widgetManager.activity.isDestroyed) {

                            pageRootView?.post {
                                removeList.forEach {
                                    getPageWidget(it).removeSelf()
                                }
                                lastWidget.parentView?.removeView(lastWidget.contentView)
                            }


                        }

                    }

                    override fun onAnimationRepeat(p0: Animation?) {

                    }
                })
                widget.contentView?.startAnimation(enterAnim)
            } else {
                enterAnim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {

                    }

                    override fun onAnimationEnd(p0: Animation?) {
                        enterAnim.cancel()
                        if (widgetManager.activity != null && !widgetManager.activity.isDestroyed) {

                            removeList.forEach {
                                getPageWidget(it).removeSelf()
                            }

                        }
                    }

                    override fun onAnimationRepeat(p0: Animation?) {

                    }
                })

                widget.contentView?.startAnimation(enterAnim)

            }

        } else {
            if (lastWidget != null) {
                lastWidget.postAction(WidgetAction.ACTION_PAGE_LEAVE)
                lastWidget.parentView?.removeView(lastWidget.contentView)
            }
            removeList.forEach {
                getPageWidget(it).removeSelf()
            }
        }

        addPage(widget, page)

        if (widget.parentView != null) {
            pageRootView = widget.parentView
        }
    }


    companion object {
        val START_NORMAL = 0
        val START_CLEAR_ALL = 1
        val START_SINGLE_TOP = 2
        val START_SINGLE_TASK = 3
        private val PAGE_SAVAE_KAEY = "widget_page_save"
    }


}