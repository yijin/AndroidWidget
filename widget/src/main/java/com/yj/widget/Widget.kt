package com.yj.widget


import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.core.view.setPadding
import com.yj.widget.page.PageWrapper


abstract class Widget : BaseWidget() {
    val layoutInflater: LayoutInflater
        get() = activity.layoutInflater

    val isStopView: Boolean
        get() = currentState == WidgetState.DESTROYED || currentState == WidgetState.DESTROYED_VIEW

    var contentView: View? = null
        private set


    open protected abstract fun onCreateView(container: ViewGroup?): View


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
        if (viewAddBuilder != null) {
            viewAddBuilder!!.add(parentView, contentView!!)
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
        if (viewAddBuilder != null) {
            //不是顶部的page,不需要添加视图树里面
            viewAddBuilder!!.add(parentView, contentView!!, false)
        }
        if (contentView?.parent != null) {
            parentView?.removeView(contentView)
        }
        this.currentState = WidgetState.CREATED_VIEW

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


    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(TAG, "widget onConfigurationChanged ${this}")

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d(TAG, "widget onRestoreInstanceState ${this}")

    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "widget onSaveInstanceState ${this}")
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


    private var viewAddBuilder: WidgetViewAddBuilder? = null


    private fun initViewAddBuilder() {

        if (viewAddBuilder == null) {
            viewAddBuilder = WidgetViewAddBuilder(this)
        }

    }


    fun margin(left: Int, top: Int, right: Int, bottom: Int): WidgetViewAddBuilder {
        initViewAddBuilder()
        if (contentView != null && contentView?.layoutParams is ViewGroup.MarginLayoutParams) {
            (contentView?.layoutParams as ViewGroup.MarginLayoutParams).setMargins(
                left,
                top,
                right,
                bottom
            )
        }
        return viewAddBuilder!!.margin(left, top, right, bottom)
    }

    fun disableAddView(): WidgetViewAddBuilder {
        initViewAddBuilder()
        return viewAddBuilder!!.disableAddView()
    }

    fun enableAddView(): WidgetViewAddBuilder {
        initViewAddBuilder()
        return viewAddBuilder!!.enableAddView()
    }


    fun index(index: Int): WidgetViewAddBuilder {
        initViewAddBuilder()
        return viewAddBuilder!!.index(index)
    }

    fun margin(size: Int): WidgetViewAddBuilder {
        initViewAddBuilder()
        if (contentView != null && contentView?.layoutParams is ViewGroup.MarginLayoutParams) {
            (contentView?.layoutParams as ViewGroup.MarginLayoutParams).setMargins(
                size,
                size,
                size,
                size
            )
        }
        return viewAddBuilder!!.margin(size)

    }

    fun marginLeft(size: Int): WidgetViewAddBuilder {
        initViewAddBuilder()
        if (contentView != null && contentView?.layoutParams is ViewGroup.MarginLayoutParams) {
            (contentView?.layoutParams as ViewGroup.MarginLayoutParams).leftMargin = size
        }
        return viewAddBuilder!!.marginLeft(size)
    }

    fun marginTop(size: Int): WidgetViewAddBuilder {
        initViewAddBuilder()
        if (contentView != null && contentView?.layoutParams is ViewGroup.MarginLayoutParams) {
            (contentView?.layoutParams as ViewGroup.MarginLayoutParams).topMargin = size
        }
        return viewAddBuilder!!.marginTop(size)
    }

    fun marginRight(size: Int): WidgetViewAddBuilder {

        initViewAddBuilder()
        if (contentView != null && contentView?.layoutParams is ViewGroup.MarginLayoutParams) {
            (contentView?.layoutParams as ViewGroup.MarginLayoutParams).rightMargin = size
        }
        return viewAddBuilder!!.marginRight(size)
    }

    fun marginBottom(size: Int): WidgetViewAddBuilder {

        initViewAddBuilder()
        if (contentView != null && contentView?.layoutParams is ViewGroup.MarginLayoutParams) {
            (contentView?.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = size
        }
        return viewAddBuilder!!.marginBottom(size)
    }

    fun weight(weight: Float): WidgetViewAddBuilder {
        initViewAddBuilder()
        if (contentView != null && contentView?.layoutParams is LinearLayout.LayoutParams) {
            (contentView?.layoutParams as LinearLayout.LayoutParams).weight = weight
        }
        return viewAddBuilder!!.weight(weight)
    }

    private fun padding(left: Int, top: Int, right: Int, bottom: Int): WidgetViewAddBuilder {

        initViewAddBuilder()
        if (contentView != null) {
            contentView?.setPadding(left, top, right, bottom)
        }
        return viewAddBuilder!!.padding(left, top, right, bottom)
    }

    fun padding(size: Int): WidgetViewAddBuilder {
        initViewAddBuilder()
        if (contentView != null) {
            contentView?.setPadding(size)
        }
        return viewAddBuilder!!.padding(size)
    }

    fun height(size: Int): WidgetViewAddBuilder {

        initViewAddBuilder()
        if (contentView != null && contentView?.layoutParams != null) {
            contentView?.layoutParams?.height = size
        }
        return viewAddBuilder!!.height(size)
    }

    fun width(size: Int): WidgetViewAddBuilder {

        initViewAddBuilder()
        if (contentView != null && contentView?.layoutParams != null) {
            contentView?.layoutParams?.width = size
        }
        return viewAddBuilder!!.width(size)
    }

    fun size(size: Int): WidgetViewAddBuilder {
        initViewAddBuilder()
        if (contentView != null && contentView?.layoutParams != null) {
            contentView?.layoutParams?.width = size
            contentView?.layoutParams?.height = size
        }
        return viewAddBuilder!!.size(size)
    }

    fun matchParent(): WidgetViewAddBuilder {

        initViewAddBuilder()
        if (contentView != null && contentView?.layoutParams != null) {
            contentView?.layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
            contentView?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        return viewAddBuilder!!.matchParent()
    }

    fun heightMatchParent(): WidgetViewAddBuilder {


        initViewAddBuilder()
        if (contentView != null && contentView?.layoutParams != null) {
            contentView?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        return viewAddBuilder!!.heightMatchParent()
    }

    fun widthMatchParent(): WidgetViewAddBuilder {

        initViewAddBuilder()
        if (contentView != null && contentView?.layoutParams != null) {
            contentView?.layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
        return viewAddBuilder!!.widthMatchParent()
    }

    fun wrapContent(): WidgetViewAddBuilder {
        initViewAddBuilder()
        if (contentView != null && contentView?.layoutParams != null) {
            contentView?.layoutParams?.width = ViewGroup.LayoutParams.WRAP_CONTENT
            contentView?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        return viewAddBuilder!!.wrapContent()
    }

    fun heightWrapContent(): WidgetViewAddBuilder {
        initViewAddBuilder()
        if (contentView != null && contentView?.layoutParams != null) {
            contentView?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        return viewAddBuilder!!.heightWrapContent()
    }

    fun widthWrapContent(): WidgetViewAddBuilder {
        initViewAddBuilder()
        if (contentView != null && contentView?.layoutParams != null) {
            contentView?.layoutParams?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        }

        return viewAddBuilder!!.widthWrapContent()
    }


    fun backgroundColor(color: Int): WidgetViewAddBuilder {
        initViewAddBuilder()
        if (contentView != null) {
            contentView?.setBackgroundColor(color)
        }
        return viewAddBuilder!!.backgroundColor(color)
    }

    fun backgroundResource(resid: Int): WidgetViewAddBuilder {
        initViewAddBuilder()
        if (contentView != null) {
            contentView?.setBackgroundResource(resid)
        }
        return viewAddBuilder!!.backgroundResource(resid)
    }

    fun background(background: Drawable?): WidgetViewAddBuilder {
        initViewAddBuilder()
        if (contentView != null) {
            contentView?.background = background
        }
        return viewAddBuilder!!.background(background)
    }


}



