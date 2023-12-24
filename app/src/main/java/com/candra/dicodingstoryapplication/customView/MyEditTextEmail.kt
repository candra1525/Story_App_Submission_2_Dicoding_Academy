package com.candra.dicodingstoryapplication.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.candra.dicodingstoryapplication.R

class MyEditTextEmail : AppCompatEditText, View.OnTouchListener {
    private lateinit var clearButton: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttribute: Int) : super(
        context,
        attributeSet,
        defStyleAttribute
    ) {
        init()
    }

    private fun init() {
        clearButton = ContextCompat.getDrawable(context, R.drawable.icon_clear_24) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) showClearIcon() else hideClearIcon()
            }

            override fun afterTextChanged(emailText: Editable?) {
                val email = emailText.toString()
                if (!isValidEmail(email)) {
                    error = context.getString(R.string.email_invalid)
                }
            }
        })
    }


    private fun isValidEmail(email: String): Boolean {
        val patternEmail = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()
        return email.matches(patternEmail)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearStart: Float
            val clearEnd: Float
            var isClearIconClick = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearEnd = (clearButton.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearEnd -> isClearIconClick = true
                }
            } else {
                clearStart = (width - paddingEnd - clearButton.intrinsicWidth).toFloat()
                when {
                    event.x > clearStart -> isClearIconClick = true
                }
            }

            if (isClearIconClick) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearButton =
                            ContextCompat.getDrawable(context, R.drawable.icon_clear_24) as Drawable
                        showClearIcon()
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        clearButton =
                            ContextCompat.getDrawable(context, R.drawable.icon_clear_24) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideClearIcon()
                        return true
                    }

                    else -> return false
                }
            } else return false
        }
        return false
    }

    private fun setDrawables(
        startText: Drawable? = null,
        topText: Drawable? = null,
        endText: Drawable? = null,
        bottomText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(startText, topText, endText, bottomText)
    }

    private fun showClearIcon() {
        setDrawables(endText = clearButton)
    }

    private fun hideClearIcon() {
        setDrawables()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}