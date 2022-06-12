package com.market.uikit.adapter

import android.view.View
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import java.util.*
/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */
open class HeaderAndFooterWrapper(var innerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mHeaderViewHolderList = ArrayList<ViewHolder>()
    private val mFooterViewHolderList = ArrayList<ViewHolder>()


    //这个map主要用于onCreateViewHolder的快速查询，牺牲一点内存
    private val mHeaderViewTypeMap = SparseArrayCompat<ViewHolder>()
    private val mFooterViewTypeMap = SparseArrayCompat<ViewHolder>()


    private val mViewTypeManager: ViewTypeManager =
        ViewTypeManager()

    protected var mRecyclerView: RecyclerView? = null

    protected val realItemCount: Int
        get() = innerAdapter.itemCount


    private val mInnerObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            notifyDataSetChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            notifyItemRangeChanged(headersCount + positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            notifyItemRangeChanged(headersCount + positionStart, itemCount, payload)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            notifyItemRangeInserted(headersCount + positionStart, itemCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            notifyItemRangeRemoved(headersCount + positionStart, itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            notifyItemMoved(headersCount + fromPosition, headersCount + toPosition)
        }
    }

    val headersCount: Int
        get() = mHeaderViewHolderList.size

    private val footersCount: Int
        get() = mFooterViewHolderList.size


    init {
        setHasStableIds(innerAdapter.hasStableIds())
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: ViewHolder? = mHeaderViewTypeMap.get(viewType)
            ?: mFooterViewTypeMap.get(viewType)
        return viewHolder ?: innerAdapter.onCreateViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isHeaderViewPos(position)) {
            mHeaderViewHolderList[position].viewType
        } else if (isFooterViewPos(position)) {
            mFooterViewHolderList[position - headersCount - realItemCount].viewType
        } else {
            val itemViewType = innerAdapter.getItemViewType(position - headersCount)
            if (isHeaderOrFooterViewType(itemViewType)) {
                throw IllegalArgumentException("HeaderAndFooterWrapper use the viewType between " + ViewTypeManager.ITEM_TYPE_MIN + " and " + ViewTypeManager.ITEM_TYPE_MAX)
            } else {
                itemViewType
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isHeaderViewPos(position)) {
            return
        }
        if (isFooterViewPos(position)) {
            return
        }
        innerAdapter.onBindViewHolder(holder, position - headersCount)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (isHeaderViewPos(position)) {
            return
        }
        if (isFooterViewPos(position)) {
            return
        }
        innerAdapter.onBindViewHolder(holder, position - headersCount, payloads)
    }

    override fun getItemCount(): Int {
        return headersCount + footersCount + realItemCount
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView
        innerAdapter.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            val gridLayoutManager = layoutManager as GridLayoutManager?
            val originSpanSizeLookup = gridLayoutManager?.spanSizeLookup
            gridLayoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val viewType = getItemViewType(position)
                    return when {
                        isHeaderOrFooterViewType(viewType) -> gridLayoutManager?.spanCount ?: 1
                        originSpanSizeLookup != null -> originSpanSizeLookup.getSpanSize(position - headersCount)
                        else -> 1
                    }
                }
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = null
        innerAdapter.onDetachedFromRecyclerView(recyclerView)
    }


    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.registerAdapterDataObserver(observer)
        //mInnerAdapter 数据发生变化时候，调用相应Wrapper相应的方法,去通知RecylcerView
        innerAdapter.registerAdapterDataObserver(mInnerObserver)
    }

    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.unregisterAdapterDataObserver(observer)
        innerAdapter.unregisterAdapterDataObserver(mInnerObserver)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        if (isHeaderOrFooterViewType(holder.itemViewType)) {
            val layoutParams = holder.itemView.layoutParams
            if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                layoutParams.isFullSpan = true
            }
        } else {
            innerAdapter.onViewAttachedToWindow(holder)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        if (isHeaderOrFooterViewType(holder.itemViewType)) {
            super.onViewDetachedFromWindow(holder)
        } else {
            innerAdapter.onViewDetachedFromWindow(holder)
        }
    }

    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        return if (isHeaderOrFooterViewType(holder.itemViewType)) {
            super.onFailedToRecycleView(holder)
        } else {
            innerAdapter.onFailedToRecycleView(holder)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (isHeaderOrFooterViewType(holder.itemViewType)) {
            super.onViewRecycled(holder)
        } else {
            innerAdapter.onViewRecycled(holder)
        }
    }


    private fun isHeaderViewPos(position: Int): Boolean {
        return position < headersCount
    }

    private fun isFooterViewPos(position: Int): Boolean {
        return position >= headersCount + realItemCount
    }


    protected fun isHeaderOrFooterViewType(viewType: Int): Boolean {
        return mViewTypeManager.isHeaderOrFooterViewType(viewType)
    }

    fun addHeaderView(view: View?) {
        addHeaderView(mHeaderViewHolderList.size, view)
    }

    fun addHeaderView(pos: Int, view: View?) {
        if (pos < 0 || pos > mHeaderViewHolderList.size) {
            return
        }
        if (view == null) {
            return
        }
        val viewType = mViewTypeManager.get()
        val viewHolder = ViewHolder(viewType, view)
        mHeaderViewHolderList.add(pos, viewHolder)
        mHeaderViewTypeMap.put(viewType, viewHolder)
        notifyItemInserted(pos)
    }

    fun getHeadViews(): List<View> {
        return mHeaderViewHolderList.map { it.itemView }
    }

    fun removeHeaderView(view: View?): Boolean {
        if (view == null) {
            return false
        }
        val indexOfFirst = mHeaderViewHolderList.indexOfFirst { it.itemView == view }
        return removeHeaderView(indexOfFirst)
    }

    fun removeHeaderView(): Boolean {
        return removeHeaderView(0)
    }

    fun removeHeaderView(pos: Int): Boolean {
        if (pos < 0 || pos >= mHeaderViewHolderList.size) {
            return false
        }
        val viewHolder = mHeaderViewHolderList.removeAt(pos)
        mHeaderViewTypeMap.remove(viewHolder.viewType)
        /**
         * header和footer的ViewHolder都不复用。防止多次add remove时，不同的View使用相同的itemViewType，如果此时ViewHolder
         * 复用的话，新的View就不会显示出来。
         */
        viewHolder.setIsRecyclable(false)
        mViewTypeManager.recycle(viewHolder.viewType)
        notifyItemRemoved(pos)
        return true
    }


    fun addFootView(view: View?) {
        addFootView(mFooterViewHolderList.size, view)
    }

    fun addFootView(pos: Int, view: View?) {
        if (pos < 0 || pos > mFooterViewHolderList.size) {
            return
        }
        if (view == null) {
            return
        }
        val viewType = mViewTypeManager.get()
        val viewHolder = ViewHolder(viewType, view)
        mFooterViewHolderList.add(viewHolder)
        mFooterViewTypeMap.put(viewType, viewHolder)
        notifyItemInserted(itemCount - footersCount + pos)
    }

    fun removeFooterView(view: View?): Boolean {
        if (view == null) {
            return false
        }
        val indexOfFirst = mFooterViewHolderList.indexOfFirst { it.itemView == view }
        return removeFooterView(indexOfFirst)
    }

    fun removeFooterView(): Boolean {
        return removeFooterView(0)
    }

    fun removeFooterView(pos: Int): Boolean {
        if (pos < 0 || pos >= mFooterViewHolderList.size) {
            return false
        }
        val viewHolder = mFooterViewHolderList.removeAt(pos)
        mFooterViewTypeMap.remove(viewHolder.viewType)
        /**
         * header和footer的ViewHolder都不复用。防止多次add remove时，不同的View使用相同的itemViewType，如果此时ViewHolder
         * 复用的话，新的View就不会显示出来。
         */
        viewHolder.setIsRecyclable(false)
        mViewTypeManager.recycle(viewHolder.viewType)
        notifyItemRemoved(pos)
        return true
    }

    fun getFootViews(): List<View> {
        return mFooterViewHolderList.map { it.itemView }
    }
}

private class ViewHolder(var viewType: Int, view: View) : RecyclerView.ViewHolder(view)

private class ViewTypeManager {

    private var mIncType = ITEM_TYPE_MIN
    private val mRecycleList = LinkedList<Int>()

    /**
     * 获取一个可用的viewType
     *
     * @return
     */
    fun get(): Int {
        return if (mRecycleList.size > 0) {
            mRecycleList.removeFirst()
        } else {
            val type = mIncType
            if (type > ITEM_TYPE_MAX) {
                throw IllegalStateException("Your viewType is too much,it's impossible in common")
            }
            mIncType++
            type
        }
    }

    fun isHeaderOrFooterViewType(viewType: Int): Boolean {
        return viewType in ITEM_TYPE_MIN..ITEM_TYPE_MAX
    }

    fun recycle(viewType: Int) {
        mRecycleList.addLast(viewType)
    }

    companion object {
        const val ITEM_TYPE_MIN = 100000
        const val ITEM_TYPE_MAX = 110000
    }
}
