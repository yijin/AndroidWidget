package com.yj.widget

import android.view.ViewGroup
import com.yj.widget.page.Page

data class WidgetCreateParams(
    val widgetManager: WidgetManager,
    val widget: Widget,
    val parentView: ViewGroup?,
    val page: Page,
    val parentWidget: Widget?
) {





}