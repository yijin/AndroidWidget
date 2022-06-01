package com.yj.widget

import android.view.ViewGroup
import android.view.animation.Animation
import androidx.annotation.AnimRes
import com.yj.widget.page.PageWidgetManager.Companion.START_NORMAL

data class WidgetCreateParams(
    val widgetManager: WidgetManager,
    val widget: Widget,
    val parentView: ViewGroup,
    val pageWidget: Widget,
    val parentWidget: Widget
) {





}