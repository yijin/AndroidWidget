package com.yj.widget

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle

internal class WidgetLoadBuilder internal constructor(
    val widgetManager: WidgetManager,
    val widget: BaseWidget,

    ) {

    val params: WidgetCreateParams =
        WidgetCreateParams(widgetManager, widget)


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
        if (widgetManager.activity.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return false
        }

        widget.create(params)

        return true
    }


}