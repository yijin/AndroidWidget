package com.yj.widget

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */
class Modifier(val widget: Widget) {
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
    private var disableAddView = false
    private var index = -1
    private var clickable: Boolean? = null
    private var onLongClickListener: View.OnLongClickListener? = null
    private var onClickListener: View.OnClickListener? = null

    private var gravity: Int? = null
    private var layoutGravity: Int? = null

    private var verticalGravity: Int? = null
    private var horizontalGravity: Int? = null


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

    fun verticalGravity(verticalGravity: Int): Modifier {
        this.verticalGravity = verticalGravity
        return this
    }

    fun horizontalGravity(horizontalGravity: Int): Modifier {
        this.horizontalGravity = horizontalGravity
        return this;
    }

    fun addRule(rule: Int, id: Int): Modifier {
        return this
    }


    fun gravity(gravity: Int): Modifier {
        if (this.gravity == null) {
            this.gravity = gravity
        } else {
            this.gravity = gravity or this.gravity!!
        }
        return this
    }

    fun gravityLeft(): Modifier {

        return gravity(Gravity.LEFT)

    }

    fun gravityRight(): Modifier {
        return gravity(Gravity.RIGHT)
    }

    fun gravityBottom(): Modifier {
        return gravity(Gravity.BOTTOM)
    }

    fun gravityTop(): Modifier {
        return gravity(Gravity.TOP)
    }

    fun gravityCenter(): Modifier {
        return gravity(Gravity.CENTER)
    }

    fun gravityCenterHorizontal(): Modifier {
        return gravity(Gravity.CENTER_HORIZONTAL)
    }

    fun gravityCenterVertical(): Modifier {
        return gravity(Gravity.CENTER_VERTICAL)
    }

    fun layoutGravity(gravity: Int): Modifier {
        if (this.layoutGravity == null) {
            this.layoutGravity = gravity
        } else {
            this.layoutGravity = gravity or this.layoutGravity!!
        }
        return this
    }

    fun layoutRight(): Modifier {
        return layoutGravity(Gravity.RIGHT)
    }

    fun layoutBotton(): Modifier {
        return layoutGravity(Gravity.BOTTOM)
    }

    fun layoutTop(): Modifier {
        return layoutGravity(Gravity.TOP)
    }

    fun layoutLeft(): Modifier {
        return layoutGravity(Gravity.LEFT)
    }

    fun layoutGravityCenter(): Modifier {
        return layoutGravity(Gravity.CENTER)
    }

    fun layoutGravityCenterHorizontal(): Modifier {
        return layoutGravity(Gravity.CENTER_HORIZONTAL)
    }

    fun layoutGravityCenterVertical(): Modifier {
        return layoutGravity(Gravity.CENTER_VERTICAL)
    }


    fun clickable(clickable: Boolean): Modifier {
        this.clickable = clickable
        return this;
    }

    fun onLongClickListener(listener: View.OnLongClickListener?): Modifier {

        this.onLongClickListener = listener;
        return this
    }

    fun onClickListener(listener: View.OnClickListener?): Modifier {
        this.onClickListener = listener
        return this
    }


    fun margin(left: Int, top: Int, right: Int, bottom: Int): Modifier {

        this.marginLeft = left
        this.marginTop = top
        this.marginRight = right
        this.marginBottom = bottom
        return this
    }

    fun margin(size: Int): Modifier {
        this.marginLeft = size
        this.marginTop = size
        this.marginRight = size
        this.marginBottom = size
        return this
    }

    fun disableAddView(): Modifier {
        this.disableAddView = true
        return this
    }

    fun enableAddView(): Modifier {
        this.disableAddView = false
        return this
    }

    fun index(index: Int): Modifier {
        this.index = index
        return this
    }

    fun marginLeft(size: Int): Modifier {

        this.marginLeft = size

        return this
    }

    fun marginTop(size: Int): Modifier {

        this.marginTop = size

        return this
    }

    fun marginRight(size: Int): Modifier {

        this.marginRight = size

        return this
    }

    fun marginBottom(size: Int): Modifier {

        this.marginBottom = size

        return this
    }

    fun weight(weight: Float): Modifier {
        this.weight = weight
        return this
    }

    fun padding(left: Int, top: Int, right: Int, bottom: Int): Modifier {

        this.paddingLeft = left
        this.paddingTop = top
        this.paddingRight = right
        this.paddingBottom = bottom
        return this
    }

    fun padding(size: Int): Modifier {
        this.paddingLeft = size
        this.paddingTop = size
        this.paddingRight = size
        this.paddingBottom = size

        return this
    }

    fun paddingLeft(size: Int): Modifier {
        this.paddingLeft = size
        return this
    }

    fun paddingRight(size: Int): Modifier {
        this.paddingRight = size
        return this
    }

    fun paddingBottom(size: Int): Modifier {
        this.paddingBottom = size
        return this
    }

    fun paddingTop(size: Int): Modifier {
        this.paddingTop = size
        return this
    }

    fun height(size: Int): Modifier {

        this.height = size
        return this
    }

    fun width(size: Int): Modifier {

        this.width = size
        return this
    }

    fun matchParent(): Modifier {

        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        this.height = ViewGroup.LayoutParams.MATCH_PARENT
        return this
    }

    fun heightMatchParent(): Modifier {


        this.height = ViewGroup.LayoutParams.MATCH_PARENT
        return this
    }

    fun widthMatchParent(): Modifier {

        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        return this
    }

    fun wrapContent(): Modifier {
        this.width = ViewGroup.LayoutParams.WRAP_CONTENT
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        return this
    }

    fun heightWrapContent(): Modifier {
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        return this
    }

    fun widthWrapContent(): Modifier {
        this.width = ViewGroup.LayoutParams.WRAP_CONTENT
        return this
    }

    fun size(size: Int): Modifier {
        this.width = size
        this.height = size
        return this
    }

    fun backgroundColor(color: Int): Modifier {
        this.backgroundColor = color
        return this
    }

    fun backgroundResource(resid: Int): Modifier {
        this.backgroundResource = resid
        return this
    }

    fun background(background: Drawable?): Modifier {

        this.background = background
        return this
    }

    fun get(): Widget {
        return widget
    }


    internal fun add(parentView: ViewGroup?, view: View, neeAddView: Boolean = true) {

        if (view.parent == null && !disableAddView && neeAddView) {
            if (parentView != null) {
                if (index != null && index!! >= 0 && index!! <= parentView.childCount) {
                    parentView.addView(view, index!!)
                } else {
                    parentView.addView(view)
                }

            } else {
                widget.activity.setContentView(view)
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
        if (paddingLeft != null || paddingRight != null || paddingBottom != null || paddingTop != null) {
            view.setPadding(
                if (paddingLeft == null) view.paddingLeft else paddingLeft!!,
                if (paddingTop == null) view.paddingTop else paddingTop!!,
                if (paddingRight == null) view.paddingRight else paddingRight!!,
                if (paddingBottom == null) view.paddingBottom else paddingBottom!!
            )
        }
        if (addLayoutParams == null) {
            if (parentView is LinearLayout) {
                addLayoutParams = LinearLayout.LayoutParams(
                    if (width == null) ViewGroup.LayoutParams.WRAP_CONTENT else width!!,
                    if (height == null) ViewGroup.LayoutParams.WRAP_CONTENT else height!!
                )
            } else if (parentView is FrameLayout) {
                addLayoutParams = FrameLayout.LayoutParams(
                    if (width == null) ViewGroup.LayoutParams.WRAP_CONTENT else width!!,
                    if (height == null) ViewGroup.LayoutParams.WRAP_CONTENT else height!!
                )
            } else if (parentView is RelativeLayout) {
                addLayoutParams = RelativeLayout.LayoutParams(
                    if (width == null) ViewGroup.LayoutParams.WRAP_CONTENT else width!!,
                    if (height == null) ViewGroup.LayoutParams.WRAP_CONTENT else height!!
                )
            } else if (parentView is RecyclerView) {
                addLayoutParams = RecyclerView.LayoutParams(
                    if (width == null) ViewGroup.LayoutParams.WRAP_CONTENT else width!!,
                    if (height == null) ViewGroup.LayoutParams.WRAP_CONTENT else height!!
                )
            } else {
                addLayoutParams = ViewGroup.MarginLayoutParams(
                    if (width == null) ViewGroup.LayoutParams.WRAP_CONTENT else width!!,
                    if (height == null) ViewGroup.LayoutParams.WRAP_CONTENT else height!!
                )
            }

        } else {
            if (width != null || height != null) {
                if (width != null) {
                    addLayoutParams.width = width!!
                }
                if (height != null) {
                    addLayoutParams.height = height!!
                }
            }
        }


        if (weight != null && addLayoutParams is LinearLayout.LayoutParams) {

            addLayoutParams.weight = weight!!
        }
        if (marginLeft != null || marginTop != null || marginRight != null || marginBottom != null) {

            if (addLayoutParams is ViewGroup.MarginLayoutParams) {
                addLayoutParams.setMargins(
                    if (marginLeft == null) addLayoutParams.leftMargin else marginLeft!!,
                    if (marginTop == null) addLayoutParams.topMargin else marginTop!!,
                    if (marginRight == null) addLayoutParams.rightMargin else marginRight!!,
                    if (marginBottom == null) addLayoutParams.bottomMargin else marginBottom!!,
                )
            }

        }

        if (layoutGravity != null) {
            if (addLayoutParams is LinearLayout.LayoutParams) {
                addLayoutParams.gravity = layoutGravity!!
            } else if (addLayoutParams is FrameLayout.LayoutParams) {
                addLayoutParams.gravity = layoutGravity!!
            }
        }
        if (gravity != null) {
            if (view is TextView) {
                view.gravity = gravity!!
            }
        }


        if (horizontalGravity != null) {
            if (view is LinearLayout) {
                view.setHorizontalGravity(horizontalGravity!!)
            }
        }
        if (verticalGravity != null) {
            if (view is LinearLayout) {
                view.setVerticalGravity(verticalGravity!!)
            }
        }


        view.layoutParams = addLayoutParams

    }


}