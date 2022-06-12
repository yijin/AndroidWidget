package com.yj.widget.page

import android.os.Parcelable
import androidx.annotation.AnimRes
import kotlinx.android.parcel.Parcelize

/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */
@Parcelize
open class PageWrapper(
    val page: Page,
    @AnimRes var exitAnimResId: Int = 0
) : Parcelable {
}