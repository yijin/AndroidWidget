package com.hi.dhl.paging3.network.data.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */
interface Repository<T : Any> {

   fun getPagingData(): Flow<PagingData<T>>

}