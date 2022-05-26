package com.yj.widget.page


import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.yj.widget.Widget
import com.yj.widget.WidgetAction
import com.yj.widget.WidgetManager
import java.util.*

internal class PageWidgetManager(val widgetManager: WidgetManager) {
    val widgetList = LinkedList<Widget>()


    fun topPageWidget(): Widget? {

        return widgetList.peekLast()
    }

    fun destroy() {
        widgetList.forEach {
            it.postAction(WidgetAction.ACTION_ACTIVITY_DESTROY)

        }
    }

    fun restore() {
        if (topPageWidget()!!.contentView.parent == null) {
            widgetManager.activity.setContentView(topPageWidget()!!.contentView)
        } else {
            (topPageWidget()!!.contentView.parent as ViewGroup).removeAllViews()
            widgetManager.activity.setContentView(topPageWidget()!!.contentView)
        }
        widgetList.forEach {

            it.parentView = topPageWidget()!!.contentView.parent as ViewGroup?
            if (it == topPageWidget()) {
                it.postAction(WidgetAction.ACTION_CREATED)
                it.postAction(WidgetAction.ACTION_CREATED_VIEW)
            } else {
                it.postAction(WidgetAction.ACTION_CREATED)
            }

        }
    }

    fun backPressed(): Boolean {
        if (widgetManager.activity.lifecycle.currentState == Lifecycle.State.DESTROYED
        ) {
            return false
        }
        if (widgetList.size < 1) {
            return false
        }
        if (widgetList.size == 1) {
            widgetManager.activity.finish()
            return true
        }
        widgetList.get(widgetList.size - 2).postAction(WidgetAction.ACTION_PAGE_RE_CREATED_VIEW)
        val last = widgetList.peekLast()
        last.removeSelf(false)
        last.postAction(WidgetAction.ACTION_PAGE_BACK)

        return true
    }

    fun backFirstWidget(classType: Class<*>): Boolean {
        if (widgetManager.activity.lifecycle.currentState == Lifecycle.State.DESTROYED
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
        if (widgetManager.activity.lifecycle.currentState == Lifecycle.State.DESTROYED
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

        return PageWidgetStartBuilder(
            widgetManager,
            widget,
            this
        )
            .replaceWidget(oldWidget)

    }

    fun start(widget: Widget): PageWidgetStartBuilder {

        return PageWidgetStartBuilder(
            widgetManager,
            widget,
            this
        )

    }


    fun startClearAll(widget: Widget): PageWidgetStartBuilder {


        return PageWidgetStartBuilder(
            widgetManager, widget, this,
            START_CLEAR_ALL
        )

    }

    fun startSingleTask(widget: Widget): PageWidgetStartBuilder {


        return PageWidgetStartBuilder(
            widgetManager, widget, this,
            START_SINGLE_TASK
        )

    }

    fun startSingleTop(widget: Widget): PageWidgetStartBuilder {


        return PageWidgetStartBuilder(
            widgetManager, widget, this,
            START_SINGLE_TOP
        )
    }


    companion object {
        val START_NORMAL = 0
        val START_CLEAR_ALL = 1
        val START_SINGLE_TOP = 2
        val START_SINGLE_TASK = 3
    }


}