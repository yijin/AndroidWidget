package com.hi.dhl.paging3.network.data.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow

/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */
class RepositoryImpl<K : Any, T : Any>(
    val pageConfig: PagingConfig,
    val pagingSource: PagingSource<K, T>,
) : Repository<T> {


    override fun getPagingData(): Flow<PagingData<T>> {
        return Pager(
            config = pageConfig,
            pagingSourceFactory = { pagingSource }
        ).flow
    }

}