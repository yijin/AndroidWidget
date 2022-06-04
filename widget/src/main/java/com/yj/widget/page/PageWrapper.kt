package com.yj.widget.page

import android.os.Parcelable
import androidx.annotation.AnimRes
import kotlinx.android.parcel.Parcelize

@Parcelize
open class PageWrapper(
    val page: Page,
    @AnimRes var exitAnimResId: Int = 0
) : Parcelable {
}