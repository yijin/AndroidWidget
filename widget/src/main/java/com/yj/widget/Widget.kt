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
import androidx.annotation.AnimRes
import androidx.core.view.setPadding
import androidx.lifecycle.*
import com.yj.widget.page.PageWidgetWrapper


abstract class Widget : BaseWidget() {
    val layoutInflater: LayoutInflater
        get() = activity.layoutInflater

    val isStopView: Boolean
        get() = currentState == WidgetState.DESTROYED || currentState == WidgetState.DESTROYED_VIEW

    var contentView: View? = null
        private set

    @AnimRes
    private var exitAnimResId = 0


    val isPageWidget: Boolean
        get() = this == pageWidget
    override var pageBundle: Bundle? = null
        get() {
            return if (isPageWidget) field else pageWidget?.pageBundle
        }


    open protected abstract fun onCreateView(container: ViewGroup?): View


    val pageAllWidgets = ArrayList<BaseWidget>()


    val isVisible: Boolean
        get() = contentView != null && contentView!!.visibility == View.VISIBLE


    var parentWidget: Widget? = null
        protected set

    var parentView: ViewGroup? = null
        get() {
            return if (field != null) field else contentView?.parent as ViewGroup?
        }
        internal set


    @SuppressLint("ResourceType")
    internal fun create(
        params: WidgetCreateParams
    ) {
        super.initWidget(params.widgetManager, params.pageWidget)

        this.parentWidget = params.parentWidget
        if (this.parentWidget != null) {
            this.parentWidget?.childWidgets?.add(this)
        }
        params.pageWidget?.pageAllWidgets?.add(this)
        this.parentView = params.parentView!!

        onCreate(widgetManager.savedInstanceState)
        this.currentState = WidgetState.CREATED
        this.contentView = onCreateView(params.parentView)
        this.currentState = WidgetState.CREATED_VIEW
        if (viewAddBuilder != null) {
            viewAddBuilder!!.add(parentView!!, contentView!!)
        } else {
            if (this.contentView?.parent == null) {
                parentView!!.addView(contentView)
            }
        }


        when (pageWidget?.currentState) {
            WidgetState.STARTED -> {
                start()
            }
            WidgetState.RESUMED -> {
                start()
                resume()
            }

        }
    }


    internal fun pageCreate(
        widgetManager: WidgetManager,
        params: PageWidgetWrapper,
        pageRootView: ViewGroup?
    ) {

        initWidget(widgetManager, this)

        this.pageBundle = pageBundle
        this.exitAnimResId = params.exitAnimResId
        this.parentView = pageRootView

        onCreate(widgetManager.savedInstanceState)
        this.currentState = WidgetState.CREATED
        this.contentView = onCreateView(pageRootView)
        this.currentState = WidgetState.CREATED_VIEW
        if (this.contentView?.parent == null) {
            if (pageRootView != null) {
                pageRootView.addView(contentView)
            } else {
                activity.setContentView(contentView)
            }
        }
        if (this.contentView?.parent != null) {
            this.parentView = contentView?.parent as ViewGroup?
        }



        when (activity.lifecycle.currentState) {
            Lifecycle.State.STARTED -> {
                start()

            }
            Lifecycle.State.RESUMED -> {
                start()
                resume()
            }

        }
    }

    internal fun pageRestore(
        widgetManager: WidgetManager,
        params: PageWidgetWrapper,
        pageRootView: ViewGroup?
    ) {

        initWidget(widgetManager, this)
        this.pageBundle = pageBundle
        this.parentView = pageRootView
        this.exitAnimResId = params.exitAnimResId
        onCreate(widgetManager.savedInstanceState)
        this.currentState = WidgetState.CREATED
        this.contentView = onCreateView(pageRootView)
        this.currentState = WidgetState.CREATED_VIEW

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


    fun replaceWidget(widget: Widget): Boolean {

        if (isPageWidget) {
            return throw RuntimeException("PageWidget cannot replace")
        }
        if (widgetManager.replaceWidget(this, widget)) {

            return true
        }
        return false
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(TAG, "widget onConfigurationChanged ${this}")
        if (isPageWidget) {
            pageAllWidgets.forEach {
                it.onConfigurationChanged(newConfig)
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d(TAG, "widget onRestoreInstanceState ${this}")
        if (isPageWidget) {
            pageAllWidgets.forEach {
                it.onRestoreInstanceState(savedInstanceState)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "widget onSaveInstanceState ${this}")
        if (isPageWidget) {
            pageAllWidgets.forEach {
                it.onSaveInstanceState(outState)
            }
        }
    }


    private fun start() {
        if (currentState == WidgetState.CREATED_VIEW || currentState == WidgetState.STOP) {
            onStart()
            currentState = WidgetState.STARTED
        }
        if (isPageWidget) {
            pageAllWidgets.forEach {

                it.postAction(WidgetAction.ACTION_START)
            }
        }
    }

    private fun resume() {

        if (currentState == WidgetState.STARTED || currentState == WidgetState.PAUSE) {
            onResume()
            currentState = WidgetState.RESUMED
        }
        if (isPageWidget) {
            pageAllWidgets.forEach {

                it.postAction(WidgetAction.ACTION_RESUME)
            }
        }
    }

    private fun pause() {
        if (currentState == WidgetState.RESUMED) {
            currentState = WidgetState.PAUSE

            onPause()
        }
        if (isPageWidget) {
            pageAllWidgets.forEach {
                it.postAction(WidgetAction.ACTION_PAUESE)
            }
        }
    }

    private fun stop() {

        if (currentState == WidgetState.PAUSE || currentState == WidgetState.STARTED) {
            currentState = WidgetState.STOP
            onStop()
        }
        if (isPageWidget) {
            pageAllWidgets.forEach {
                it.postAction(WidgetAction.ACTION_STOP)
            }
        }
    }


    private fun destroyView() {
        if (currentState == WidgetState.STOP || currentState == WidgetState.CREATED_VIEW) {
            onDestroyView()
            currentState = WidgetState.DESTROYED_VIEW
        }
        if (isPageWidget) {
            pageAllWidgets.forEach {
                if (it is Widget) {
                    it.postAction(WidgetAction.ACTION_DESTROY_VIEW)
                }
            }
        }
    }

    private fun destroy() {

        if (currentState == WidgetState.DESTROYED_VIEW) {
            onDestroy()
            currentState = WidgetState.DESTROYED
        }


        if (isPageWidget) {

            pageAllWidgets.forEach {
                it.postAction(WidgetAction.ACTION_DESTROY)
            }
        }


    }


    private fun pageWidgetReCreateView() {
        if (!isPageWidget) {
            return
        }

        if (contentView != null) {

            if (parentView != null) {
                parentView?.addView(contentView, 0)
            } else {
                activity.setContentView(contentView)
            }
        }
    }

    private fun pageWidgetBack() {
        if (!isPageWidget) {
            return
        }

        when (activity.lifecycle.currentState) {
            Lifecycle.State.STARTED -> {
                start()
            }
            Lifecycle.State.RESUMED -> {
                start()
                resume()
            }

        }
    }

    private fun pageWidgetLeave() {

        if (!isPageWidget) {
            return
        }

        when (currentState) {

            WidgetState.STARTED -> {
                stop()

            }
            WidgetState.RESUMED -> {
                pause()
                stop()
            }
            WidgetState.PAUSE -> {

                stop()
            }

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
            WidgetAction.ACTION_PAGE_LEAVE -> {
                pageWidgetLeave()
            }
            WidgetAction.ACTION_PAGE_BACK -> {
                pageWidgetBack()
            }

            WidgetAction.ACTION_DESTROY_VIEW -> {
                destroyView()
            }
            WidgetAction.ACTION_DESTROY -> {
                destroy()
            }

            WidgetAction.ACTION_ACTIVITY_DESTROY -> {
                destroy()
                if (isPageWidget) {
                    parentView?.removeView(contentView)
                    parentView = null
                }
            }
            WidgetAction.ACTION_PAGE_RE_CREATED_VIEW -> {
                pageWidgetReCreateView()
            }
        }


    }

    private var isRemove = false
    override fun remove() {
        if ((isPageWidget && !widgetManager.containsPageWidget(this)) || isRemove) {
            return

        }
        if (isPageWidget && inTopPageWidget) {
            widgetManager.onBackPressed()
        } else {
            removeSelf()
        }
    }

    @SuppressLint("ResourceType")
    internal fun removeSelf(noAnim: Boolean) {
        if (isRemove) {
            return
        }
        isRemove = true
        parentWidget?.childWidgets?.remove(this)
        super.removeSelf()
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
            else -> {
                destroy()

            }
        }
        if (exitAnimResId > 0 && !noAnim) {

            val exitAnim = AnimationUtils.loadAnimation(activity, exitAnimResId)
            exitAnim?.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                    if (!isActivityDestroyed) {
                        parentView?.removeView(contentView)
                    }
                }

                override fun onAnimationRepeat(p0: Animation?) {

                }
            })
            contentView?.startAnimation(exitAnim)
        } else {
            parentView?.removeView(contentView)
        }
    }

    override fun removeSelf() {

        removeSelf(true)
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



