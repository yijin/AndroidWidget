package com.yj.widget.page

import com.yj.widget.WidgetManager

data class PageWidgetCreateParams(val widgetManager: WidgetManager, val startType: Int) {

    var enterAnimResId: Int = 0
}
