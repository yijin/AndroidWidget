package com.yj.widget.paging

import androidx.paging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class PagingManager<K : Any, T : Any>(
    val scope: CoroutineScope,
    val pageConfig: PagingConfig,
    val pagingSource: PagingSource<K, T>,
) {

    private val pageFlow: Flow<PagingData<T>>
        get() {
            return Pager(
                config = pageConfig,
                pagingSourceFactory = { pagingSource }
            ).flow
        }

    private var pagingData: PagingData<T>? = null
    private var adapter: PagingViewAdapter<K, T>? = null
    private val _removeItemFlow = MutableStateFlow(mutableListOf<T>())
    private val removedItemsFlow: Flow<MutableList<T>> get() = _removeItemFlow

    fun bindPaging(adapter: PagingViewAdapter<K, T>) {
        this.adapter = adapter
        scope.launch {
            pageFlow
                .cachedIn(scope)
                .combine(removedItemsFlow) { pagingData, removed ->
                    pagingData.filter {
                        it !in removed
                    }
                }
                .collectLatest {
                    pagingData = it
                    adapter.submitData(it)
                }
        }
    }

    fun removeItem(item: T?) {
        if (item == null) {
            return
        }


        pagingData = pagingData?.filter {
            it != item

        }
        scope.launch {
            adapter?.submitData(pagingData!!)
        }
    }


    fun addItem(index: Int, item: T?) {
        if (item == null) {
            return
        }
        if (index < 0) {
            return
        }


        var postion = 0

        pagingData = pagingData?.flatMap {

            if (postion == index) {
                postion++
                listOf(item, it)
            } else {
                postion++
                listOf(it)
            }

        }
        scope.launch {
            adapter?.submitData(pagingData!!)
        }


    }


}
