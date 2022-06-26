package com.yj.widget


import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.setPadding
import com.evernote.android.state.StateSaver
import com.yj.widget.page.PageWrapper

/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */
abstract class Widget : BaseWidget() {
    val layoutInflater: LayoutInflater
        get() = activity.layoutInflater

    val isStopView: Boolean
        get() = currentState == WidgetState.DESTROYED || currentState == WidgetState.DESTROYED_VIEW

    var contentView: View? = null
        private set


    open protected abstract fun onCreateView(container: ViewGroup?): View

    open protected fun onCreatedView() {

    }


    val isVisible: Boolean
        get() = contentView != null && contentView!!.visibility == View.VISIBLE


    var parentWidget: Widget? = null
        protected set

    var parentView: ViewGroup? = null
        get() {
            val view = if (field != null) field else contentView?.parent as ViewGroup?
            if (view == null && page.rootWidget == this) {
                return page.pageRootView
            }
            return view
        }
        internal set


    @SuppressLint("ResourceType")
    internal fun create(
        params: WidgetCreateParams
    ) {
        super.initWidget(params.widgetManager, params.page)

        this.parentWidget = params.parentWidget
        if (this.parentWidget != null) {
            this.parentWidget?.childWidgets?.add(this)
        }
        page.pageAllWidgets.add(this)
        this.parentView = params.parentView

        onCreate(widgetManager.savedInstanceState)
        this.currentState = WidgetState.CREATED
        this.contentView = onCreateView(params.parentView)
        this.currentState = WidgetState.CREATED_VIEW
        if (viewModifier != null) {
            viewModifier!!.add(parentView, contentView!!)
        } else {
            if (this.contentView?.parent == null) {
                if (parentView != null) {
                    parentView!!.addView(contentView)
                } else {
                    activity.setContentView(contentView)
                }
            }
        }
        if (this.parentView == null) {
            this.parentView = contentView?.parent as ViewGroup?
        }
        onCreatedView()


        when (page.currentState) {
            WidgetState.STARTED -> {
                start()
            }
            WidgetState.RESUMED -> {
                start()
                resume()
            }

        }
    }


    internal fun pageRestore(
        widgetManager: WidgetManager,
        params: PageWrapper,
        pageRootView: ViewGroup?
    ) {

        initWidget(widgetManager, params.page)

        this.parentView = pageRootView
        onCreate(widgetManager.savedInstanceState)
        this.currentState = WidgetState.CREATED
        this.contentView = onCreateView(pageRootView)
        if (viewModifier != null) {
            //不是顶部的page,不需要添加视图树里面
            viewModifier!!.add(parentView, contentView!!, false)
        }
        if (contentView?.parent != null) {
            parentView?.removeView(contentView)
        }
        this.currentState = WidgetState.CREATED_VIEW
        onCreatedView()

    }

    override fun backPressed() {
        widgetManager.backPressed()
    }


    open fun show() {

        if (contentView != null && contentView!!.visibility != View.VISIBLE) {
            contentView!!.visibility = View.VISIBLE
        }

    }

    open fun hide() {
        if (contentView != null && contentView!!.visibility != View.GONE) {
            contentView!!.visibility = View.GONE
        }
    }


    open fun onUserVisibleHint(isVisible: Boolean) {

    }


    fun <T : View> `$`(id: Int): T? {
        return if (contentView == null) {
            null
        } else contentView!!.findViewById(id)

    }

    fun <T : View> findViewById(id: Int): T? {
        return if (contentView == null) {
            null
        } else contentView!!.findViewById(id)

    }


    fun loadChildWidget(containerView: ViewGroup, widget: Widget): Boolean {
        return widgetManager.loadChild(containerView, widget, this)
    }





    private fun start() {
        if (currentState == WidgetState.CREATED_VIEW || currentState == WidgetState.STOP) {
            onStart()
            currentState = WidgetState.STARTED
        }

    }

    private fun resume() {

        if (currentState == WidgetState.STARTED || currentState == WidgetState.PAUSE) {
            onResume()
            currentState = WidgetState.RESUMED
        }

    }

    private fun pause() {
        if (currentState == WidgetState.RESUMED) {
            currentState = WidgetState.PAUSE

            onPause()
        }

    }

    private fun stop() {

        if (currentState == WidgetState.PAUSE || currentState == WidgetState.STARTED) {
            currentState = WidgetState.STOP
            onStop()
        }

    }


    private fun destroyView() {
        if (currentState == WidgetState.STOP || currentState == WidgetState.CREATED_VIEW) {
            onDestroyView()
            currentState = WidgetState.DESTROYED_VIEW
        }

    }

    private fun destroy() {

        if (currentState == WidgetState.DESTROYED_VIEW) {
            onDestroy()
            currentState = WidgetState.DESTROYED
        }


    }


    override fun postAction(action: WidgetAction) {

        when (action) {

            WidgetAction.ACTION_START -> {
                start()
            }
            WidgetAction.ACTION_RESUME -> {
                resume()
            }
            WidgetAction.ACTION_PAUESE -> {
                pause()
            }
            WidgetAction.ACTION_STOP -> {
                stop()
            }

            WidgetAction.ACTION_DESTROY_VIEW -> {
                destroyView()
            }
            WidgetAction.ACTION_DESTROY -> {
                destroy()
            }

        }


    }


    fun replaceWidget(widget: Widget): Boolean {


        if (widgetManager.replaceWidget(this, widget)) {

            return true
        }
        return false
    }

    private var isRemove = false
    override fun remove() {
        if (page.rootWidget == this) {
            if (inTopPage) {
                backPressed()
            } else {
                page.remove()
            }
        } else {
            removeSelf()
        }
    }


    internal fun removeSelf() {

        if (isRemove) {
            return
        }
        isRemove = true
        super.remove()
        parentWidget?.childWidgets?.remove(this)
        when (currentState) {
            WidgetState.CREATED_VIEW -> {
                destroyView()
                destroy()

            }
            WidgetState.STARTED -> {
                stop()
                destroyView()
                destroy()

            }
            WidgetState.RESUMED -> {
                pause()
                stop()
                destroyView()
                destroy()

            }
            WidgetState.PAUSE -> {
                stop()
                destroyView()
                destroy()
            }
            WidgetState.STOP -> {
                destroyView()
                destroy()
            }
            WidgetState.DESTROYED_VIEW -> {
                destroy()
            }
            WidgetState.DESTROYED -> {

            }
        }

        parentView?.removeView(contentView)


    }


    private var viewModifier: Modifier? = null


    open internal fun initModifier() {

        if (viewModifier == null) {
            viewModifier = Modifier(this)
        }

    }

    open fun modifier(): Modifier {
        initModifier()
        return viewModifier!!
    }


    open fun setOnLongClickListener(listener: View.OnLongClickListener?) {

        contentView?.setOnLongClickListener(listener)
    }

    open fun setOnClickListener(listener: View.OnClickListener?) {
        contentView?.setOnClickListener(listener)
    }


    fun margin(left: Int, top: Int, right: Int, bottom: Int) {

        if (contentView != null && contentView?.layoutParams is ViewGroup.MarginLayoutParams) {
            (contentView?.layoutParams as ViewGroup.MarginLayoutParams).setMargins(
                left,
                top,
                right,
                bottom
            )
        }

    }

    fun margin(size: Int) {

        if (contentView != null && contentView?.layoutParams is ViewGroup.MarginLayoutParams) {
            (contentView?.layoutParams as ViewGroup.MarginLayoutParams).setMargins(
                size,
                size,
                size,
                size
            )
        }

    }

    fun marginLeft(size: Int) {

        if (contentView != null && contentView?.layoutParams is ViewGroup.MarginLayoutParams) {
            (contentView?.layoutParams as ViewGroup.MarginLayoutParams).leftMargin = size
        }

    }

    fun marginTop(size: Int) {

        if (contentView != null && contentView?.layoutParams is ViewGroup.MarginLayoutParams) {
            (contentView?.layoutParams as ViewGroup.MarginLayoutParams).topMargin = size
        }

    }

    fun marginRight(size: Int) {


        if (contentView != null && contentView?.layoutParams is ViewGroup.MarginLayoutParams) {
            (contentView?.layoutParams as ViewGroup.MarginLayoutParams).rightMargin = size
        }

    }

    fun marginBottom(size: Int) {

        if (contentView != null && contentView?.layoutParams is ViewGroup.MarginLayoutParams) {
            (contentView?.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = size
        }

    }

    fun weight(weight: Float) {

        if (contentView != null && contentView?.layoutParams is LinearLayout.LayoutParams) {
            (contentView?.layoutParams as LinearLayout.LayoutParams).weight = weight
        }

    }

    private fun padding(left: Int, top: Int, right: Int, bottom: Int) {

        if (contentView != null) {
            contentView?.setPadding(left, top, right, bottom)
        }
    }

    fun padding(size: Int) {

        if (contentView != null) {
            contentView?.setPadding(size)
        }
    }

    fun height(size: Int) {

        if (contentView != null && contentView?.layoutParams != null) {
            contentView?.layoutParams?.height = size
        }
    }

    fun width(size: Int) {

        if (contentView != null && contentView?.layoutParams != null) {
            contentView?.layoutParams?.width = size
        }

    }

    fun size(size: Int) {

        if (contentView != null && contentView?.layoutParams != null) {
            contentView?.layoutParams?.width = size
            contentView?.layoutParams?.height = size
        }

    }

    fun matchParent() {

        if (contentView != null && contentView?.layoutParams != null) {
            contentView?.layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
            contentView?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    fun heightMatchParent() {


        if (contentView != null && contentView?.layoutParams != null) {
            contentView?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        }

    }

    fun widthMatchParent() {

        if (contentView != null && contentView?.layoutParams != null) {
            contentView?.layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    fun wrapContent() {
        if (contentView != null && contentView?.layoutParams != null) {
            contentView?.layoutParams?.width = ViewGroup.LayoutParams.WRAP_CONTENT
            contentView?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }

    }

    fun heightWrapContent() {

        if (contentView != null && contentView?.layoutParams != null) {
            contentView?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    fun widthWrapContent() {

        if (contentView != null && contentView?.layoutParams != null) {
            contentView?.layoutParams?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        }

    }


    fun backgroundColor(color: Int) {

        if (contentView != null) {
            contentView?.setBackgroundColor(color)
        }

    }

    fun backgroundResource(resid: Int) {

        if (contentView != null) {
            contentView?.setBackgroundResource(resid)
        }

    }

    fun background(background: Drawable?) {

        if (contentView != null) {
            contentView?.background = background
        }

    }


}



