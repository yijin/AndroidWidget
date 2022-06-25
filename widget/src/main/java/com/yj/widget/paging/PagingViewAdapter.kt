package com.yj.widget.paging

import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
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
class PagingViewAdapter<K : Any, T : Any>(
    val pagingWidget: PagingWidget<K, T>,
    diffCallback: DiffUtil.ItemCallback<T>,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    workerDispatcher: CoroutineDispatcher = Dispatchers.Default
) :
    PagingDataAdapter<T, RecyclerView.ViewHolder>(diffCallback, mainDispatcher, workerDispatcher) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val widget = onCreateViewHolderWidget(viewType)
        widget.setAdapter(this)
        pagingWidget.loadChildWidget(
            pagingWidget.contentView as ViewGroup,
            widget.modifier().disableAddView().get()
        )
        return PagingViewHolder(widget)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder: PagingViewHolder<T> = holder as PagingViewHolder<T>
        bindData(viewHolder.widget, position)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        val viewHolder: PagingViewHolder<T> = holder as PagingViewHolder<T>
        viewHolder.widget.onDetachedFromWindow()
    }


    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        val viewHolder: PagingViewHolder<T> = holder as PagingViewHolder<T>
        viewHolder.widget.remove()
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val viewHolder: PagingViewHolder<T> = holder as PagingViewHolder<T>
        viewHolder.widget.onAttachedToWindow()
    }

    protected fun bindData(widget: PagingViewHolderWidget<T>, position: Int) {
        widget.bindData(position, getItem(position))
    }

    fun onCreateViewHolderWidget(viewType: Int): PagingViewHolderWidget<T> {

        return pagingWidget.onCreateViewHolderWidget(viewType)
    }





}