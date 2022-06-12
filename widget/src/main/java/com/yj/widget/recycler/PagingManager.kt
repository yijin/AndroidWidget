package com.yj.widget.recycler

import androidx.paging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

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

    private val _removeItemFlow = MutableStateFlow(mutableListOf<T>())
    private val removedItemsFlow: Flow<MutableList<T>> get() = _removeItemFlow

    fun bindPaging(adapter: RecyclerViewAdapter<K, T>) {
        scope.launch {
            pageFlow
                .cachedIn(scope)
                .combine(removedItemsFlow) { pagingData, removed ->
                    pagingData.filter {
                        it !in removed
                    }
                }
                .collectLatest {

                    adapter.submitData(it)
                }
        }
    }

    fun remove(item: T?) {
        if (item == null) {
            return
        }

        val removes = _removeItemFlow.value
        val list = mutableListOf(item)
        list.addAll(removes)
        _removeItemFlow.value = list
    }


}
