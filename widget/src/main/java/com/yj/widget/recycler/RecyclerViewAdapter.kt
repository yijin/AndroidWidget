package com.yj.widget.recycler

import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */
class RecyclerViewAdapter<K : Any, T : Any>(
    val recyclerWidget: RecyclerWidget<K, T>,
    diffCallback: DiffUtil.ItemCallback<T>,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    workerDispatcher: CoroutineDispatcher = Dispatchers.Default
) :
    PagingDataAdapter<T, RecyclerView.ViewHolder>(diffCallback, mainDispatcher, workerDispatcher) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val widget = onCreateViewHolderWidget(viewType)
        widget.setAdapter(this)
        recyclerWidget.loadChildWidget(
            recyclerWidget.contentView as ViewGroup,
            widget.disableAddView().get()
        )
        return RecyclerViewHolder(widget)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder: RecyclerViewHolder<T> = holder as RecyclerViewHolder<T>
        bindData(viewHolder.widget, position)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        val viewHolder: RecyclerViewHolder<T> = holder as RecyclerViewHolder<T>
        viewHolder.widget.onDetachedFromWindow()
    }


    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        val viewHolder: RecyclerViewHolder<T> = holder as RecyclerViewHolder<T>
        viewHolder.widget.remove()
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val viewHolder: RecyclerViewHolder<T> = holder as RecyclerViewHolder<T>
        viewHolder.widget.onAttachedToWindow()
    }

    protected fun bindData(widget: RecyclerViewHolderWidget<T>, position: Int) {
        widget.bindData(position, getItem(position))
    }

    fun onCreateViewHolderWidget(viewType: Int): RecyclerViewHolderWidget<T> {

        return recyclerWidget.onCreateViewHolderWidget(viewType)
    }





}