package com.example.plantasya_mobileapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream

object BitmapConverter {

    fun drawableToByteArray(context: Context, drawableId: Int): ByteArray {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        var bitmap = when (drawable) {
            is BitmapDrawable -> drawable.bitmap
            else -> {
                val b = Bitmap.createBitmap(
                    drawable?.intrinsicWidth ?: 1,
                    drawable?.intrinsicHeight ?: 1,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(b)
                drawable?.setBounds(0, 0, canvas.width, canvas.height)
                drawable?.draw(canvas)
                b
            }
        }

        // Scale down if too large (Max 1024px)
        val maxSize = 1024
        if (bitmap.width > maxSize || bitmap.height > maxSize) {
            val scale = maxSize.toFloat() / Math.max(bitmap.width, bitmap.height)
            val matrix = Matrix()
            matrix.postScale(scale, scale)
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        val stream = ByteArrayOutputStream()
        // Use JPEG for photos to save massive amounts of space/memory
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return stream.toByteArray()
    }

    fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap? {
        return if (byteArray != null) {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } else {
            null
        }
    }
}
