package com.yj.widget.widget.recycler

import androidx.recyclerview.widget.RecyclerView

class RecyclerViewHolder<T>(val widget: RecyclerViewHolderWidget<T>) :
    RecyclerView.ViewHolder(
        widget.contentView!!
    ) {

   init {

        widget.setViewHolder(this)
    }
}