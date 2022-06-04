package com.yj.widget.page

import com.yj.widget.WidgetManager

data class PageCreateParams(val widgetManager: WidgetManager, val startType: Int) {

    var enterAnimResId: Int = 0
}
