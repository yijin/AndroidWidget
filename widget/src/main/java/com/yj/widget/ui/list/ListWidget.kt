package com.yj.widget.ui.list;

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.market.uikit.adapter.HeaderAndFooterWrapper
import com.yj.widget.Widget
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


abstract class ListWidget<T : Any>(
    val diffCallback: DiffUtil.ItemCallback<T>,
    val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    val workerDispatcher: CoroutineDispatcher = Dispatchers.Default
) : Widget() {

    protected lateinit var mAdapter: RecyclerViewAdapter<T>
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var headerAndFooterWrapper: HeaderAndFooterWrapper
    protected lateinit var layoutManager: RecyclerView.LayoutManager


    override fun onCreateView(container: ViewGroup?): View {
        mAdapter = createAdapter()
        recyclerView = createRecyclerView()
        headerAndFooterWrapper = HeaderAndFooterWrapper(mAdapter)
        layoutManager = createLayoutManager()
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.setAdapter(headerAndFooterWrapper)
        return recyclerView!!
    }

    fun createRecyclerView(): RecyclerView {
        return RecyclerView(activity)
    }

    protected fun createAdapter(): RecyclerViewAdapter<T> {
        return RecyclerViewAdapter(this, diffCallback, mainDispatcher, workerDispatcher)

    }


    fun verticalLinearLayoutManager(): LinearLayoutManager {
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL)
        return linearLayoutManager
    }

    fun horizontalLinearLayoutManager(): LinearLayoutManager {
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL)
        return linearLayoutManager
    }

    open protected fun createLayoutManager(): RecyclerView.LayoutManager {
        return verticalLinearLayoutManager()
    }

    fun addHeaderView(view: View?) {
        headerAndFooterWrapper?.addHeaderView(view)
    }

    fun addHeaderWidget(widget: Widget) {
        loadChildWidget(recyclerView, widget.disableAddView().get())
        addHeaderView(widget.contentView)
    }

    fun addHeaderWidget(pos: Int, widget: Widget) {
        loadChildWidget(recyclerView, widget.disableAddView().get())
        addHeaderView(pos, widget.contentView)
    }


    private fun removeHeaderView(view: View?): Boolean {
        return headerAndFooterWrapper.removeHeaderView(view)
    }

    private fun removeHeaderView(pos: Int) {
        headerAndFooterWrapper.removeHeaderView(pos)
    }

    fun removeHeaderWidget(widget: Widget) {
        removeHeaderView(widget.contentView)
        widget.removeSelf()
    }

    private fun addHeaderView(pos: Int, view: View?) {
        headerAndFooterWrapper.addHeaderView(pos, view)
    }

    private fun addFooterView(view: View?) {
        headerAndFooterWrapper.addFootView(view)
    }

    fun addFooterWidget(widget: Widget) {
        loadChildWidget(recyclerView,widget.disableAddView().get())
        addFooterView(widget.contentView)
    }

    fun addFooterWidget(pos: Int, widget: Widget) {
        loadChildWidget(recyclerView, widget.disableAddView().get())
        addFooterView(pos, widget.contentView)
    }

    fun removeFooterWidget(widget: Widget) {
        removedFooterView(widget.contentView)
        widget.removeSelf()
    }

    private fun addFooterView(pos: Int, view: View?) {
        headerAndFooterWrapper.addFootView(pos, view)
    }

    private fun removedFooterView(view: View?) {
        headerAndFooterWrapper.removeFooterView(view)
    }

    private fun removedFooterView(pos: Int) {
        headerAndFooterWrapper.removeFooterView(pos)
    }


    abstract fun onCreateViewHolderWidget(viewType: Int): RecyclerViewHolderWidget<T>


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
