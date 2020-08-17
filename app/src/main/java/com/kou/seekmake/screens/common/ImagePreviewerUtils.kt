package com.kou.seekmake.screens.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import kotlin.math.roundToInt

class ImagePreviewerUtils {
    companion object {
        fun getBlurredScreenDrawable(context: Context, screen: View): BitmapDrawable {
            val screenshot = takeScreenshot(screen)
            val blurred = blurBitmap(context, screenshot)
            return BitmapDrawable(context.resources, blurred)
        }

        private fun takeScreenshot(view: View): Bitmap {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            return bitmap
        }

        private fun blurBitmap(context: Context, bitmap: Bitmap): Bitmap {
            val bitmapScale = 0.3f
            val blurRadius = 10f

            val width = (bitmap.width * bitmapScale).roundToInt()
            val height = (bitmap.height * bitmapScale).roundToInt()

            val inputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
            val outputBitmap = Bitmap.createBitmap(inputBitmap)

            val rs = RenderScript.create(context)
            val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
            val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
            theIntrinsic.setRadius(blurRadius)
            theIntrinsic.setInput(tmpIn)
            theIntrinsic.forEach(tmpOut)
            tmpOut.copyTo(outputBitmap)

            return outputBitmap
        }
    }
}