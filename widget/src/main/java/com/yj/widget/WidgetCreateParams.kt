package com.yj.widget

import android.view.ViewGroup
import android.view.animation.Animation
import androidx.annotation.AnimRes
import com.yj.widget.page.PageWidgetManager.Companion.START_NORMAL

data class WidgetCreateParams(
    val widgetManager: WidgetManager,
    var widget: BaseWidget? = null,
) {
    var pageWidget: Widget? = null
    var parentWidget: BaseWidget? = null
    var parentView: ViewGroup? = null

    var index = -1


}