package com.yj.widget.recycler.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.PagingData
import com.hi.dhl.paging3.network.data.repository.Repository

/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */
class RecyclerViewModel<T : Any>(repository: Repository<T>) : ViewModel() {
    val liveData: LiveData<PagingData<T>> =
        repository.getPagingData().asLiveData()
}