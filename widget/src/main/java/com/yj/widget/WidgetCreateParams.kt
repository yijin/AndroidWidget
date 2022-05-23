package com.yj.widget

import android.view.ViewGroup
import android.view.animation.Animation
import androidx.annotation.AnimRes

data class WidgetCreateParams(
    val activity: WidgetActivity,
    val widgetManager: WidgetManager,
    val widget: BaseWidget,
) {
    var pageWidget: Widget? = null
    var parentWidget: BaseWidget? = null

    var parentView: ViewGroup? = null

    var index = -1

    var attachToRoot = true



    var exitAnim: Animation? = null


}