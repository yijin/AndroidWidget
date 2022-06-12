package com.yj.widget.recycler.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import com.hi.dhl.paging3.network.data.repository.Repository
import com.hi.dhl.paging3.network.data.repository.RepositoryImpl

/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */
class RepositoryFactory<K : Any, T : Any>(val pagingSource: PagingSource<K, T>) {

    // 传递 PagingConfig 和 Data Mapper
    fun makeRepository(): Repository<T> =
        RepositoryImpl(
            config,
            pagingSource
        )

    companion object {

        private const val PAGE_SIZE = 30
        val config = PagingConfig(
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

