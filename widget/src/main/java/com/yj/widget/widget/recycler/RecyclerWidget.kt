package com.yj.widget.widget.recycler;

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.market.uikit.adapter.HeaderAndFooterWrapper
import com.yj.widget.Widget
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


abstract class RecyclerWidget<T : Any>(
    val diffCallback: DiffUtil.ItemCallback<T>,
    val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    val workerDispatcher: CoroutineDispatcher = Dispatchers.Default
) : Widget() {

    protected lateinit var mAdapter: RecyclerViewAdapter<T>
        private set
    protected lateinit var recyclerView: RecyclerView
        private set
    protected lateinit var headerAndFooterWrapper: HeaderAndFooterWrapper
        private set
    protected lateinit var layoutManager: RecyclerView.LayoutManager
        private set


    override fun onCreateView(container: ViewGroup?): View {
        mAdapter = createAdapter()
        recyclerView = createRecyclerView()
        headerAndFooterWrapper = HeaderAndFooterWrapper(mAdapter)
        layoutManager = createLayoutManager()
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.setAdapter(headerAndFooterWrapper)
        return recyclerView!!
    }

    open protected fun createRecyclerView(): RecyclerView {
        return RecyclerView(activity)
    }

    open protected fun createLayoutManager(): RecyclerView.LayoutManager {
        return verticalLinearLayoutManager()
    }

    open protected fun createAdapter(): RecyclerViewAdapter<T> {
        return RecyclerViewAdapter(this, diffCallback, mainDispatcher, workerDispatcher)
    }

    abstract fun onCreateViewHolderWidget(viewType: Int): RecyclerViewHolderWidget<T>


    protected fun verticalLinearLayoutManager(): LinearLayoutManager {
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL)
        return linearLayoutManager
    }

    protected fun horizontalLinearLayoutManager(): LinearLayoutManager {
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL)
        return linearLayoutManager
    }


    fun addHeaderWidget(widget: Widget) {
        loadChildWidget(recyclerView, widget.disableAddView().get())
        headerAndFooterWrapper?.addHeaderView(widget.contentView)
    }

    fun addHeaderWidget(pos: Int, widget: Widget) {
        loadChildWidget(recyclerView, widget.disableAddView().get())
        headerAndFooterWrapper?.addHeaderView(pos, widget.contentView)
    }


    fun removeHeaderWidget(widget: Widget) {
        headerAndFooterWrapper.removeHeaderView(widget.contentView)
        widget.removeSelf()
    }


    fun addFooterWidget(widget: Widget) {
        loadChildWidget(recyclerView, widget.disableAddView().get())
        headerAndFooterWrapper.addFootView(widget.contentView)
    }

    fun addFooterWidget(pos: Int, widget: Widget) {
        loadChildWidget(recyclerView, widget.disableAddView().get())
        headerAndFooterWrapper.addFootView(pos, widget.contentView)
    }

    fun removeFooterWidget(widget: Widget) {
        headerAndFooterWrapper.removeFooterView(widget.contentView)
        widget.removeSelf()
    }


    fun smoothScrollToPosition(position: Int) {
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(position)
        }
    }

    fun scrollToPosition(position: Int) {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(position)
        }
    }
}
