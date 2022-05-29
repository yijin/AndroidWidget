package com.yj.widget

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

class WidgetViewAddBuilder(private val widget: Widget) {
    private var height: Int? = null
    private var width: Int? = null
    private var marginLeft: Int? = null
    private var marginTop: Int? = null
    private var marginRight: Int? = null
    private var marginBottom: Int? = null

    private var paddingLeft: Int? = null
    private var paddingTop: Int? = null
    private var paddingRight: Int? = null
    private var paddingBottom: Int? = null
    private var weight: Float? = null
    private var backgroundColor: Int? = null
    private var backgroundResource: Int? = null
    private var background: Drawable? = NO_DRAWABLE

    private companion object {
        val NO_DRAWABLE = object : Drawable() {
            override fun draw(p0: Canvas) {

            }

            override fun setAlpha(p0: Int) {

            }

            override fun setColorFilter(p0: ColorFilter?) {

            }

            override fun getOpacity(): Int {
                return PixelFormat.TRANSLUCENT
            }
        }
    }


    fun margin(left: Int, top: Int, right: Int, bottom: Int): WidgetViewAddBuilder {

        this.marginLeft = left
        this.marginTop = top
        this.marginRight = right
        this.marginBottom = bottom
        return this
    }

    fun margin(size: Int): WidgetViewAddBuilder {
        this.marginLeft = size
        this.marginTop = size
        this.marginRight = size
        this.marginBottom = size
        return this
    }

    fun marginLeft(size: Int): WidgetViewAddBuilder {

        this.marginLeft = size

        return this
    }

    fun marginTop(size: Int): WidgetViewAddBuilder {

        this.marginTop = size

        return this
    }

    fun marginRight(size: Int): WidgetViewAddBuilder {

        this.marginRight = size

        return this
    }

    fun marginBottom(size: Int): WidgetViewAddBuilder {

        this.marginBottom = size

        return this
    }

    fun weight(weight: Float): WidgetViewAddBuilder {
        this.weight = weight
        return this
    }

    fun padding(left: Int, top: Int, right: Int, bottom: Int): WidgetViewAddBuilder {

        this.paddingLeft = left
        this.paddingTop = top
        this.paddingRight = right
        this.paddingBottom = bottom
        return this
    }

    fun padding(size: Int): WidgetViewAddBuilder {
        this.paddingLeft = size
        this.paddingTop = size
        this.paddingRight = size
        this.paddingBottom = size

        return this
    }

    fun height(size: Int): WidgetViewAddBuilder {

        this.height = size
        return this
    }

    fun width(size: Int): WidgetViewAddBuilder {

        this.width = size
        return this
    }

    fun matchParent(): WidgetViewAddBuilder {

        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        this.height = ViewGroup.LayoutParams.MATCH_PARENT
        return this
    }

    fun heightMatchParent(): WidgetViewAddBuilder {


        this.height = ViewGroup.LayoutParams.MATCH_PARENT
        return this
    }

    fun widthMatchParent(): WidgetViewAddBuilder {

        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        return this
    }

    fun wrapContent(): WidgetViewAddBuilder {
        this.width = ViewGroup.LayoutParams.WRAP_CONTENT
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        return this
    }

    fun heightWrapContent(): WidgetViewAddBuilder {
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        return this
    }

    fun widthWrapContent(): WidgetViewAddBuilder {
        this.width = ViewGroup.LayoutParams.WRAP_CONTENT
        return this
    }

    fun size(size: Int): WidgetViewAddBuilder {
        this.width = size
        this.height = size
        return this
    }

    fun backgroundColor(color: Int): WidgetViewAddBuilder {
        this.backgroundColor = color
        return this
    }

    fun backgroundResource(resid: Int): WidgetViewAddBuilder {
        this.backgroundResource = resid
        return this
    }

    fun background(background: Drawable?): WidgetViewAddBuilder {

        this.background = background
        return this
    }

    fun get(): Widget {
        return widget
    }


    internal fun add(parentView: ViewGroup, view: View, index: Int) {

        if (view.parent == null) {
            if (index != null && index!! >= 0 && index!! <= parentView.childCount) {
                parentView.addView(view, index!!)
            } else {
                parentView.addView(view)
            }
        }
        if (backgroundColor != null) {
            view.setBackgroundColor(backgroundColor!!)
        }
        if (backgroundResource != null) {
            view.setBackgroundResource(backgroundResource!!)
        }
        if (background != NO_DRAWABLE) {
            view.setBackground(background)
        }

        var addLayoutParams = view.layoutParams
        if (paddingLeft != null) {
            view.setPadding(paddingLeft!!, paddingTop!!, paddingRight!!, paddingBottom!!)
        }

        if (weight != null && parentView is LinearLayout) {
            if (addLayoutParams == null) {
                addLayoutParams = LinearLayout.LayoutParams(
                    if (width == null) ViewGroup.LayoutParams.WRAP_CONTENT else width!!,
                    if (height == null) ViewGroup.LayoutParams.WRAP_CONTENT else height!!
                )
            }
            (addLayoutParams as LinearLayout.LayoutParams).weight = weight!!
        }
        if (marginLeft != null || marginTop != null || marginRight != null || marginBottom != null) {
            if (addLayoutParams == null) {
                addLayoutParams = ViewGroup.MarginLayoutParams(
                    if (width == null) ViewGroup.LayoutParams.WRAP_CONTENT else width!!,
                    if (height == null) ViewGroup.LayoutParams.WRAP_CONTENT else height!!
                )

            }
            (addLayoutParams as ViewGroup.MarginLayoutParams).setMargins(
                if (marginLeft == null) addLayoutParams.leftMargin else marginLeft!!,
                if (marginTop == null) addLayoutParams.topMargin else marginTop!!,
                if (marginRight == null) addLayoutParams.rightMargin else marginRight!!,
                if (marginBottom == null) addLayoutParams.bottomMargin else marginBottom!!,
            )
        }
        if (width != null || height != null) {
            if (addLayoutParams == null) {
                addLayoutParams = ViewGroup.LayoutParams(
                    if (width == null) ViewGroup.LayoutParams.WRAP_CONTENT else width!!,
                    if (height == null) ViewGroup.LayoutParams.WRAP_CONTENT else height!!
                )
            } else {
                if (width != null) {
                    addLayoutParams.width = width!!
                } else if (height != null) {
                    addLayoutParams.height = height!!
                }
            }
        }
        view.layoutParams = addLayoutParams

    }


}