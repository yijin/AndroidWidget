package com.yj.widget.page

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.AnimRes
import com.yj.widget.Widget
import kotlinx.android.parcel.Parcelize

@Parcelize
class PageWidgetWrapper(
    val widgetType: Class<out Widget>,
    val bundle: Bundle?,
    @AnimRes var exitAnimResId: Int = 0
) : Parcelable {
}