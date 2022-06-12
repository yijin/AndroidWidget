package com.yj.widget

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.yj.widget.page.Page

/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */
internal class WidgetLoadBuilder internal constructor(
    val widgetManager: WidgetManager,
    val widget: Widget,
    val parentView: ViewGroup,
    val page: Page,
    val parentWidget: Widget

) {

    val params: WidgetCreateParams =
        WidgetCreateParams(widgetManager, widget, parentView, page, parentWidget)



    fun load(): Boolean {
        if (widgetManager.activity.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return false
        }

        widget.create(params)

        return true
    }


}