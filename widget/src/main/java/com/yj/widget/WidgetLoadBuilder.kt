package com.yj.widget

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle

internal class WidgetLoadBuilder internal constructor(
    val widgetManager: WidgetManager,
    val widget: Widget,
    val parentView: ViewGroup,
    val pageWidget: Widget,
    val parentWidget: Widget

) {

    val params: WidgetCreateParams =
        WidgetCreateParams(widgetManager, widget, parentView, pageWidget, parentWidget)



    fun load(): Boolean {
        if (widgetManager.activity.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return false
        }

        widget.create(params)

        return true
    }


}