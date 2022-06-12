package com.yj.widget.page

import com.yj.widget.WidgetManager

/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */
data class PageCreateParams(val widgetManager: WidgetManager, val startType: Int) {

    var enterAnimResId: Int = 0
}
