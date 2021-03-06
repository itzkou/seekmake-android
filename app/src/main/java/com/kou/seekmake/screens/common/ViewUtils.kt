package com.kou.seekmake.screens.common

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.Window
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.kou.seekmake.R
import com.kou.seekmake.common.formatRelativeTimestamp
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.CropCircleTransformation
import java.util.*


fun Context.showToast(text: String?, duration: Int = Toast.LENGTH_SHORT) {
    text?.let { Toast.makeText(this, it, duration).show() }
}

fun coordinateBtnAndInputs(btn: Button, vararg inputs: EditText) {

    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            btn.isEnabled = inputs.all { it.text.isNotEmpty() && it.length() >= 3 }

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    }
    inputs.forEach { it.addTextChangedListener(watcher) }
    btn.isEnabled = inputs.all { it.text.isNotEmpty() && it.length() >= 3 }

}

fun coordinatePwd(btn: Button, input: EditText) {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            btn.isEnabled = input.text.length >= 6
            if (input.text.length < 6) {
                val snacko = Snackbar.make(btn, "Password is at least 6 characters", Snackbar.LENGTH_SHORT)
                snacko.config(btn.context)
                snacko.show()
            }

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    }
    input.addTextChangedListener(watcher)
    btn.isEnabled = input.text.length >= 6
}

fun coordinateName(btn: Button, input: EditText) {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (input.text.toString().indexOf(" ") == -1) {
                val snacko = Snackbar.make(btn, "Firstname and Lastname are at least 3 characters", Snackbar.LENGTH_SHORT)
                snacko.config(btn.context)
                snacko.show()
            }
            btn.isEnabled = input.text.length >= 6
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    }
    input.addTextChangedListener(watcher)
    btn.isEnabled = input.text.length >= 6
}

fun coordinatePhone(btn: Button, input: EditText) {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (input.text.length < 8) {
                val snacko = Snackbar.make(btn, "Phone is t least 8 numbers", Snackbar.LENGTH_SHORT)
                snacko.config(btn.context)
                snacko.show()
            }
            btn.isEnabled = input.text.length >= 8
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    }
    input.addTextChangedListener(watcher)
    btn.isEnabled = input.text.length >= 8
}

fun TextView.setDate(date: Date? = null) {
    val dateSpannable = date?.let {
        val dateText = formatRelativeTimestamp(date, Date())
        val spannableString = SpannableString(dateText)
        spannableString.setSpan(null,
                0, dateText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString
    }


    text = SpannableStringBuilder().apply {

        dateSpannable?.let {
            append(" ")
            append(it)
        }
    }
}

fun TextView.setCaptionText(username: String, caption: String, date: Date? = null) {
    val usernameSpannable = SpannableString(username)
    val captionSpannable = SpannableString(caption)

    usernameSpannable.setSpan(StyleSpan(Typeface.BOLD), 0, usernameSpannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    usernameSpannable.setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            widget.context.showToast(context.getString(R.string.username_is_clicked))
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }


    }, 0, usernameSpannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    captionSpannable.setSpan(ResourcesCompat.getFont(context, R.font.poppins_light), 0, captionSpannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    val dateSpannable = date?.let {
        val dateText = formatRelativeTimestamp(date, Date())
        val spannableString = SpannableString(dateText)
        spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.grey)),
                0, dateText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString
    }

    text = SpannableStringBuilder().apply {
        append(usernameSpannable)
        append(" ")
        append(caption)
        dateSpannable?.let {
            append(" ")
            append(it)
        }
    }
    movementMethod = LinkMovementMethod.getInstance()
}

fun Editable.toStringOrNull(): String? {
    val str = toString()
    return if (str.isEmpty()) null else str
}

fun ImageView.loadUserPhoto(photoUrl: String?) =
        ifNotDestroyed {
            Glide.with(this).load(photoUrl).fallback(R.drawable.person).into(this)
        }

fun ImageView.loadImgRound(photoUrl: String?, radius: Int) =
        ifNotDestroyed {
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(radius))
            Glide.with(this).load(photoUrl).apply(requestOptions).fallback(R.drawable.person).into(this)
        }

fun ImageView.loadCircle(photoUrl: String?) =
        ifNotDestroyed {
            Glide.with(this).load(photoUrl).apply(RequestOptions.bitmapTransform(CropCircleTransformation())).fallback(R.drawable.person).into(this)
        }

fun ImageView.loadblur(url: String?) =
        ifNotDestroyed {
            Glide.with(this).load(url).apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3))).fallback(R.drawable.person).into(this)
        }


fun ImageView.loadImage(image: String?) =
        ifNotDestroyed {

            Glide.with(this).load(image).centerCrop().into(this)
        }

fun ImageView.fitCenter(im: String?) = ifNotDestroyed {
    val requestOptions = RequestOptions().fitCenter()
    Glide.with(this).load(im).apply(requestOptions).into(this)
}


fun ImageView.loadImageOrHide(image: String?) =
        if (image != null) {
            visibility = View.VISIBLE
            loadImage(image)
        } else {
            visibility = View.GONE
        }

fun Snackbar.config(context: Context) {
    this.view.background = ContextCompat.getDrawable(context, R.color.colorPrimary)
    this.setActionTextColor(ContextCompat.getColor(context, R.color.white))
    val text = this.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    text.setTextColor(ContextCompat.getColor(context, R.color.white))
    text.maxLines = 1
    text.textSize = 12f
    text.fontFeatureSettings
    text.typeface = ResourcesCompat.getFont(this.context, R.font.poppins)

}

fun setupBlur(blurView: BlurView, context: Context, view: Window) {
    val decorView: View = view.decorView
    val windowBackground: Drawable = decorView.background

    blurView.setupWith(decorView.findViewById(android.R.id.content))
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(context))
            .setBlurRadius(15f)
            .setHasFixedTransformationMatrix(true)
}


private fun View.ifNotDestroyed(block: () -> Unit) {
    if (!(context as Activity).isDestroyed) {
        block()
    }
}