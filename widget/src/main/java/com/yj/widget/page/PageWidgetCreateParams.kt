package com.yj.widget.page

import android.view.animation.Animation
import androidx.annotation.AnimRes
import com.yj.widget.WidgetManager

data class PageWidgetCreateParams(val widgetManager: WidgetManager, val startType: Int) {


    var attachToRoot = true

    var exitAnimResId: Int = 0

    var enterAnimResId: Int = 0


}
