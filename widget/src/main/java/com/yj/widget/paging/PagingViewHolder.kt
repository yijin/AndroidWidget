package com.yj.widget.paging

import androidx.recyclerview.widget.RecyclerView

/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */
class PagingViewHolder<T>(val widget: PagingViewHolderWidget<T>) :
    RecyclerView.ViewHolder(
        widget.contentView!!
    ) {

   init {

        widget.setViewHolder(this)
    }
}