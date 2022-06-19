package com.yj.widget.paging;

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.lifecycle.*
import androidx.paging.*
import androidx.recyclerview.widget.*
import com.yj.widget.Widget
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex


/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */
//https://github.com/android/architecture-components-samples/blob/main/PagingSample/app/src/main/java/paging/android/example/com/pagingsample/MainActivity.kt
open abstract class PagingWidget<K : Any, T : Any> : Widget() {
    open protected var mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    open protected var workerDispatcher: CoroutineDispatcher = Dispatchers.Default

    protected lateinit var mAdapter: PagingViewAdapter<K, T>
        private set
    protected lateinit var recyclerView: RecyclerView
        private set

    protected lateinit var layoutManager: RecyclerView.LayoutManager
        private set
    private lateinit var mViewModel: MyViewModel
    private lateinit var pagingSource: PagingSource<K, T>
    private lateinit var pagingManager: PagingManager<K, T>
    var itemCount: Int = 0
        get() {
            return mAdapter.itemCount
        }
        private set

    init {
        matchParent()
    }

    class MyViewModel : ViewModel() {

        var liveData: LiveData<Any>? = null

    }


    inner class MyItemCallback : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return this@PagingWidget.areItemsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return this@PagingWidget.areContentsTheSame(oldItem, newItem)
        }
    }

    inner class MyPagingSource : PagingSource<K, T>() {
        override fun getRefreshKey(state: PagingState<K, T>): K? {
            return this@PagingWidget.getRefreshKey(state)
        }

        override suspend fun load(params: LoadParams<K>): LoadResult<K, T> {
            return this@PagingWidget.load(params)
        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mViewModel = ViewModelProvider(
            activity,
            ViewModelProvider.AndroidViewModelFactory(activity.application)
        ).get(
            MyViewModel::class.java
        )
        /*if (mViewModel.liveData == null) {
            pagingSource = onCreatePagingSource()
            pagingManager = PagingManager(activity.lifecycleScope, pagingConfig, pagingSource)

            mViewModel.liveData =
                RepositoryFactory(onCreatePagingSource()).makeRepository().getPagingData()
                    .asLiveData()
        }*/

        pagingSource = onCreatePagingSource()
        pagingManager = PagingManager(activity.lifecycleScope, pagingConfig, pagingSource)


    }


    override fun onCreateView(container: ViewGroup?): View {
        mAdapter = createAdapter()
        recyclerView = createRecyclerView()

        layoutManager = createLayoutManager()
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.setAdapter(mAdapter)
        /* (mViewModel.liveData as LiveData<PagingData<T>>?)?.observe(activity, { data ->
             submitData(data)
         })*/
        pagingManager.bindPaging(mAdapter)

        return recyclerView!!
    }


    open protected fun createRecyclerView(): RecyclerView {
        return RecyclerView(activity)
    }

    open protected fun createLayoutManager(): RecyclerView.LayoutManager {
        return verticalLinearLayoutManager()
    }

    open protected fun createAdapter(): PagingViewAdapter<K, T> {
        return PagingViewAdapter(this, onCreateItemCallback(), mainDispatcher, workerDispatcher)
    }

    abstract fun onCreateViewHolderWidget(viewType: Int): PagingViewHolderWidget<T>

    open protected fun getRefreshKey(state: PagingState<K, T>): K? {
        return null
    }

    open suspend fun load(params: PagingSource.LoadParams<K>): PagingSource.LoadResult<K, T> {
        return PagingSource.LoadResult.Page(ArrayList(), null, null)
    }

    open protected fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.equals(newItem)
    }

    open protected fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    open protected fun onCreatePagingSource(): PagingSource<K, T> {
        return MyPagingSource()
    }

    open protected fun onCreateItemCallback(): DiffUtil.ItemCallback<T> {
        return MyItemCallback()
    }


    fun withLoadStateFooter(
        footer: LoadStateAdapter<*>
    ): ConcatAdapter {
        return mAdapter.withLoadStateFooter(footer)

    }

    fun withLoadStateHeader(
        header: LoadStateAdapter<*>
    ): ConcatAdapter {
        return mAdapter.withLoadStateHeader(header)
    }

    fun withLoadStateHeaderAndFooter(
        header: LoadStateAdapter<*>,
        footer: LoadStateAdapter<*>
    ): ConcatAdapter {
        return mAdapter.withLoadStateHeaderAndFooter(header, footer)

    }


    fun refresh() {
        mAdapter.refresh()
    }

    fun retry() {
        mAdapter.retry()
    }

    fun submitData(lifecycle: Lifecycle, pagingData: PagingData<T>) {
        mAdapter.submitData(lifecycle, pagingData)
    }

    fun submitData(pagingData: PagingData<T>) {
        mAdapter.submitData(activity.lifecycle, pagingData)
    }

    fun addLoadStateListener(listener: (CombinedLoadStates) -> Unit) {
        mAdapter.addLoadStateListener(listener)
    }


    fun peek(@IntRange(from = 0) index: Int): T? {
        return mAdapter.peek(index)
    }

    /**
     * Remove a previously registered [CombinedLoadStates] listener.
     *
     * @param listener Previously registered listener.
     * @see addLoadStateListener
     */
    fun removeLoadStateListener(listener: (CombinedLoadStates) -> Unit) {
        mAdapter.removeLoadStateListener(listener)
    }


    private fun verticalLinearLayoutManager(): LinearLayoutManager {
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL)
        return linearLayoutManager
    }

    private fun horizontalLinearLayoutManager(): LinearLayoutManager {
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL)
        return linearLayoutManager
    }


    fun addHeaderWidget(widget: Widget) {
        loadChildWidget(recyclerView, widget.disableAddView().get())
        //  headerAndFooterWrapper?.addHeaderView(widget.contentView)
    }

    fun addHeaderWidget(pos: Int, widget: Widget) {
        loadChildWidget(recyclerView, widget.disableAddView().get())
        // headerAndFooterWrapper?.addHeaderView(pos, widget.contentView)
    }


    fun removeHeaderWidget(widget: Widget) {
        //headerAndFooterWrapper.removeHeaderView(widget.contentView)
        widget.removeSelf()
    }


    fun addFooterWidget(widget: Widget) {
        loadChildWidget(recyclerView, widget.disableAddView().get())
        // headerAndFooterWrapper.addFootView(widget.contentView)
    }

    fun addFooterWidget(pos: Int, widget: Widget) {
        loadChildWidget(recyclerView, widget.disableAddView().get())
        // headerAndFooterWrapper.addFootView(pos, widget.contentView)
    }

    fun removeFooterWidget(widget: Widget) {
        // headerAndFooterWrapper.removeFooterView(widget.contentView)
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


    fun removeItem(item: T) {
        pagingManager.removeItem(item)
    }


    fun addItem(index: Int, item: T) {
        pagingManager.addItem(index, item)


    }


    companion object {

        private const val PAGE_SIZE = 30
        val pagingConfig = PagingConfig(
            pageSize = PAGE_SIZE,
            // 预刷新的距离，距离最后一个 item 多远时加载数据
            prefetchDistance = 5,

            /**
             * 初始化加载数量，默认为 pageSize * 3
             *
             * internal const val DEFAULT_INITIAL_PAGE_MULTIPLIER = 3
             * val initialLoadSize: Int = pageSize * DEFAULT_INITIAL_PAGE_MULTIPLIER
             */
            initialLoadSize = 50,
            // 开启占位符
            enablePlaceholders = false,

            maxSize = PAGE_SIZE * 3
        )
    }

}
