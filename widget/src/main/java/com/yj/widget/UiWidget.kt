package com.yj.widget

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.setPadding

abstract class UiWidget : Widget() {

    private var viewAddBuilder: WidgetViewAddBuilder? = null


    override fun addView(index: Int) {
        if (viewAddBuilder != null) {
            viewAddBuilder!!.add(parentView!!, contentView!!, index)
        } else {
            super.addView(index)
        }
    }


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