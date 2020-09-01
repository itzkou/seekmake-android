package com.kou.seekmake.screens.common

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
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
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.kou.seekmake.R
import com.kou.seekmake.common.formatRelativeTimestamp
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import com.yinglan.shadowimageview.ShadowImageView
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
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
            //Todo optimize when user enters only fname with 6 characters
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


fun ImageView.loadImage(image: String?) =
        ifNotDestroyed {

            Glide.with(this).load(image).centerCrop().into(this)
        }

fun ShadowImageView.loadImage(image: String?) =
        ifNotDestroyed {

            Picasso.get().load(image).into(object : com.squareup.picasso.Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    this@loadImage.setImageDrawable(ContextCompat.getDrawable(this@loadImage.context, R.drawable.ic_launcher_background))

                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    this@loadImage.setImageBitmap(bitmap)
                }

            })
        }

fun RoundedImageView.loadImageRounded(image: String?) =
        ifNotDestroyed {
            Glide.with(this).load(image).centerCrop().into(this)
        }


fun ImageView.loadImageOrHide(image: String?) =
        if (image != null) {
            visibility = View.VISIBLE
            loadImage(image)
        } else {
            visibility = View.GONE
        }

fun Snackbar.config(context: Context) {

    this.view.background = ContextCompat.getDrawable(context, R.color.black)

    this.setActionTextColor(ContextCompat.getColor(context, R.color.white))
    val text = this.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    text.setTextColor(ContextCompat.getColor(context, R.color.white))
    text.maxLines = 1
    text.textSize = 12f

    ViewCompat.setElevation(this.view, 6f)
}

fun setupBlur(blurView: BlurView, context: Context, view: Window) {
    val decorView: View = view.decorView
    val windowBackground: Drawable = decorView.background

    blurView.setupWith(decorView.findViewById(android.R.id.content))
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(context))
            .setBlurRadius(20f)
            .setHasFixedTransformationMatrix(true)
}


private fun View.ifNotDestroyed(block: () -> Unit) {
    if (!(context as Activity).isDestroyed) {
        block()
    }
}