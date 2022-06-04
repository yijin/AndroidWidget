package com.yj.widget.page


import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.Lifecycle
import com.yj.widget.*
import java.util.*
import kotlin.collections.ArrayList

internal class PageManager(val widgetManager: WidgetManager) {

    private val pageData = LinkedList<PageWrapper>()

    var pageRootView: ViewGroup? = null
        private set


    internal fun onSaveInstanceState(outState: Bundle) {

        val list = ArrayList<PageWrapper>()
        pageData.forEach {
            list.add(it)
        }
        outState.putParcelableArrayList(PAGE_SAVAE_KAEY, list)
        pageData.forEach {
            it.page.onSaveInstanceState(outState)
        }
    }

    internal fun onRestoreInstanceState(savedInstanceState: Bundle) {
        pageData.forEach {
            it.page.onRestoreInstanceState(savedInstanceState)
        }
    }

    internal fun onConfigurationChanged(newConfig: Configuration) {
        pageData.forEach {
            it.page.onConfigurationChanged(newConfig)
        }

    }


    val pageSize: Int
        get() {
            return pageData.size
        }


    fun topPage(): Page? {
        if (pageData.peekLast() == null) {
            return null
        }
        return pageData.peekLast().page
    }

    private fun clear() {
        while (!pageData.isEmpty()) {
            pageData.peekLast().page.remove()
        }
        pageData.clear()
    }


    private fun getPage(index: Int): Page? {
        if (index < 0 || index > pageSize - 1) {
            return null
        }
        return pageData.get(index).page
    }


    fun restoreNoConfigurationChange(savedInstanceState: Bundle) {
        clear()
        val list = savedInstanceState.getParcelableArrayList<PageWrapper>(PAGE_SAVAE_KAEY)
        if (list.isNullOrEmpty()) {
            return
        }
        list.forEach {
            pageData.add(it)
        }

        pageData.peekLast().page.initPage(this, pageData.peekLast())
        pageData.peekLast().page.pageCreate(null)
        pageRootView = pageData.peekLast().page.rootWidget!!.parentView!!

        pageData.forEach {
            if (it == pageData.peekLast()) {

            } else {
                it.page.initPage(this, it)
                it.page.pageRestore(it, pageRootView!!)

            }
        }

    }


    internal fun restoreConfigurationChange() {


        topPage()?.restoreConfigurationChange()

    }


    fun backPressed(): Boolean {
        if (widgetManager.activity.lifecycle.currentState == Lifecycle.State.DESTROYED
        ) {
            return false
        }

        if (pageSize <= 1) {
            widgetManager.activity.finish()
            return true
        }

        var prePage = getPage(pageSize - 2)
        prePage?.pageReCreateView()
        topPage()!!.backPressed()
        prePage?.pageBack()
        return true
    }


    internal fun removePage(page: Page) {
        pageData.forEach {
            if (it.page == page) {
                pageData.remove(it)
                return@forEach
            }
        }
    }

    fun backPage(page: Page): Boolean {
        if (widgetManager.activity.lifecycle.currentState == Lifecycle.State.DESTROYED
        ) {
            return false
        }
        var index = -1
        for (i in pageData.indices) {
            if (pageData.get(i).page == page) {
                index = i
                break
            }
        }
        if (index >= 0) {

            val backCount = pageData.size - 1 - index
            if (backCount > 0) {
                index = 1
                while (index != backCount) {

                    pageData.get(pageData.size - 2).page.resume()
                    index++
                }

                backPressed()
            }

            return true
        } else {
            return false
        }
    }

    fun backFirstPage(classType: Class<out Page>): Boolean {
        if (widgetManager.activity.lifecycle.currentState == Lifecycle.State.DESTROYED
        ) {
            return false
        }
        var index = -1
        for (i in pageData.indices) {
            if (pageData.get(i).page::class.java == classType) {
                index = i
                break
            }
        }
        if (index >= 0) {

            val backCount = pageData.size - 1 - index
            if (backCount > 0) {
                index = 1
                while (index != backCount) {

                    pageData.get(pageData.size - 2).page.resume()
                    index++
                }

                backPressed()
            }

            return true
        } else {
            return false
        }
    }

    fun backLastPage(classType: Class<out Page>): Boolean {
        if (widgetManager.activity.lifecycle.currentState == Lifecycle.State.DESTROYED
        ) {
            return false
        }

        var index = -1
        for (i in pageData.indices) {
            if (pageData.get(i)::class.java == classType) {
                index = i
            }
        }
        if (index >= 0) {

            val backCount = pageData.size - 1 - index
            if (backCount > 0) {
                index = 1
                while (index != backCount) {
                    pageData.get(pageData.size - 2).page.remove()
                    index++
                }
                backPressed()
            }
            return true
        } else {
            return false
        }
    }


    fun start(page: Page): PageStartBuilder {

        return PageStartBuilder(
            widgetManager,
            PageWrapper(page),
            this
        )

    }


    fun startClearAll(page: Page): PageStartBuilder {


        return PageStartBuilder(
            widgetManager, PageWrapper(page), this,
            START_CLEAR_ALL
        )

    }

    fun startSingleTask(page: Page): PageStartBuilder {


        return PageStartBuilder(
            widgetManager, PageWrapper(page), this,
            START_SINGLE_TASK
        )

    }

    fun startSingleTop(page: Page): PageStartBuilder {

        return PageStartBuilder(
            widgetManager, PageWrapper(page), this,
            START_SINGLE_TOP
        )
    }

    internal fun start(pageWrapper: PageWrapper, params: PageCreateParams) {
        val removeList = ArrayList<PageWrapper>()
        when (params.startType) {
            START_CLEAR_ALL -> {
                var index = 0
                while (index < pageData.size) {
                    removeList.add(pageData.get(index))
                    index++
                }
            }
            START_SINGLE_TOP -> {
                var index = pageData.size - 1
                while (index >= 0 && pageData.get(index).page::class.java == pageWrapper.page::class.java
                ) {
                    removeList.add(pageData.get(index))
                    index--
                }
            }
            START_SINGLE_TASK -> {

                var index = -1
                for (i in pageData.indices) {

                    if (pageData.get(i).page::class.java == pageWrapper.page::class.java
                    ) {
                        index = i
                        break
                    }
                }
                if (index >= 0) {

                    while (index < pageData.size) {
                        removeList.add(pageData.get(index))
                        index++
                    }
                }
            }

        }
        removeList.forEach {
            pageData.remove(it)
        }

        val lastPage = topPage()
        pageData.add(pageWrapper)
        pageWrapper.page.initPage(this, pageWrapper)
        pageWrapper.page.pageCreate(pageRootView)
        val widget = pageWrapper.page.rootWidget!!
        if (pageRootView == null && widget.parentView != null) {
            pageRootView = widget.parentView
        }

        val enterAnim = if (params.enterAnimResId > 0)
            AnimationUtils.loadAnimation(widgetManager.activity, params.enterAnimResId) else null

        if (widget.contentView != null && widget.parentView != null && enterAnim != null) {


            if (lastPage != null) {
                lastPage.pageLeave()

                enterAnim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {

                    }

                    override fun onAnimationEnd(p0: Animation?) {
                        enterAnim.cancel()
                        if (widgetManager.activity != null && !widgetManager.activity.isDestroyed) {

                            lastPage?.removeView()
                            removeList.forEach {
                                it.page.remove()
                            }

                        }

                    }

                    override fun onAnimationRepeat(p0: Animation?) {

                    }
                })
                widget.contentView?.startAnimation(enterAnim)
            } else {
                enterAnim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {

                    }

                    override fun onAnimationEnd(p0: Animation?) {
                        enterAnim.cancel()
                        if (widgetManager.activity != null && !widgetManager.activity.isDestroyed) {

                            removeList.forEach {
                                it.page.remove()
                            }

                        }
                    }

                    override fun onAnimationRepeat(p0: Animation?) {

                    }
                })

                widget.contentView?.startAnimation(enterAnim)

            }

        } else {
            lastPage?.pageLeave()
            lastPage?.removeView()
            removeList.forEach {
                it.page.remove()
            }
        }


    }


    companion object {
        val START_NORMAL = 0
        val START_CLEAR_ALL = 1
        val START_SINGLE_TOP = 2
        val START_SINGLE_TASK = 3
        private val PAGE_SAVAE_KAEY = "widget_page_save"
    }


}