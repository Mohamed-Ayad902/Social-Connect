package com.mayad.instagram.android.extension

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.mayad.instagram.R

fun TextView.setFormattedText(
    actionText: String,
    userFullName: String,
    onUserClick: (() -> Unit?),
    onRestOfTextClick: (() -> Unit?)
) {
    val spannable = SpannableStringBuilder(actionText)

    // Find the start and end of the user's full name within the actionText
    val start = actionText.indexOf(userFullName)
    val end = start + userFullName.length

    // Set the user's full name in bold and make it clickable
    if (start != -1) {
        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onUserClick.invoke()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = context.getColor(R.color.black)
                    ds.isFakeBoldText = true
                }
            },
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    // Set the click listener for the rest of the action text
    spannable.setSpan(
        object : ClickableSpan() {
            override fun onClick(widget: View) {
                onRestOfTextClick.invoke()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = context.getColor(R.color.darker_grey)
            }
        },
        end,
        spannable.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT
    text = spannable
}