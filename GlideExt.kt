package com.mayad.instagram.android.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mayad.instagram.R
import com.mayad.instagram.core.common.domain.Media
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

fun Bitmap.fastBlur(scale: Float = 0.3f, radius: Int = 12): Bitmap? {
    var bitmap1 = this
    val width = (bitmap1.width * scale).roundToInt()
    val height = (bitmap1.height * scale).roundToInt()
    bitmap1 = Bitmap.createScaledBitmap(bitmap1, width, height, false)
    val bitmap = bitmap1.config?.let { bitmap1.copy(it, true) }
    if (radius < 1) {
        return null
    }
    val w = bitmap?.width ?: 0
    val h = bitmap?.height ?: 0
    val pix = IntArray(w * h)
    bitmap?.getPixels(pix, 0, w, 0, 0, w, h)
    val wm = w - 1
    val hm = h - 1
    val wh = w * h
    val div = radius + radius + 1
    val r = IntArray(wh)
    val g = IntArray(wh)
    val b = IntArray(wh)
    var rsum: Int
    var gsum: Int
    var bsum: Int
    var x: Int
    var y: Int
    var i: Int
    var p: Int
    var yp: Int
    var yi: Int
    val vmin = IntArray(max(w, h))
    var divsum = div + 1 shr 1
    divsum *= divsum
    val dv = IntArray(256 * divsum)
    i = 0
    while (i < 256 * divsum) {
        dv[i] = i / divsum
        i++
    }
    yi = 0
    var yw: Int = yi
    val stack = Array(div) {
        IntArray(
            3
        )
    }
    var stackpointer: Int
    var stackstart: Int
    var sir: IntArray
    var rbs: Int
    val r1 = radius + 1
    var routsum: Int
    var goutsum: Int
    var boutsum: Int
    var rinsum: Int
    var ginsum: Int
    var binsum: Int
    y = 0
    while (y < h) {
        bsum = 0
        gsum = bsum
        rsum = gsum
        boutsum = rsum
        goutsum = boutsum
        routsum = goutsum
        binsum = routsum
        ginsum = binsum
        rinsum = ginsum
        i = -radius
        while (i <= radius) {
            p = pix[yi + Math.min(wm, Math.max(i, 0))]
            sir = stack[i + radius]
            sir[0] = p and 0xff0000 shr 16
            sir[1] = p and 0x00ff00 shr 8
            sir[2] = p and 0x0000ff
            rbs = r1 - Math.abs(i)
            rsum += sir[0] * rbs
            gsum += sir[1] * rbs
            bsum += sir[2] * rbs
            if (i > 0) {
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
            } else {
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
            }
            i++
        }
        stackpointer = radius
        x = 0
        while (x < w) {
            r[yi] = dv[rsum]
            g[yi] = dv[gsum]
            b[yi] = dv[bsum]
            rsum -= routsum
            gsum -= goutsum
            bsum -= boutsum
            stackstart = stackpointer - radius + div
            sir = stack[stackstart % div]
            routsum -= sir[0]
            goutsum -= sir[1]
            boutsum -= sir[2]
            if (y == 0) {
                vmin[x] = min(x + radius + 1, wm)
            }
            p = pix[yw + vmin[x]]
            sir[0] = p and 0xff0000 shr 16
            sir[1] = p and 0x00ff00 shr 8
            sir[2] = p and 0x0000ff
            rinsum += sir[0]
            ginsum += sir[1]
            binsum += sir[2]
            rsum += rinsum
            gsum += ginsum
            bsum += binsum
            stackpointer = (stackpointer + 1) % div
            sir = stack[stackpointer % div]
            routsum += sir[0]
            goutsum += sir[1]
            boutsum += sir[2]
            rinsum -= sir[0]
            ginsum -= sir[1]
            binsum -= sir[2]
            yi++
            x++
        }
        yw += w
        y++
    }
    x = 0
    while (x < w) {
        bsum = 0
        gsum = bsum
        rsum = gsum
        boutsum = rsum
        goutsum = boutsum
        routsum = goutsum
        binsum = routsum
        ginsum = binsum
        rinsum = ginsum
        yp = -radius * w
        i = -radius
        while (i <= radius) {
            yi = Math.max(0, yp) + x
            sir = stack[i + radius]
            sir[0] = r[yi]
            sir[1] = g[yi]
            sir[2] = b[yi]
            rbs = r1 - Math.abs(i)
            rsum += r[yi] * rbs
            gsum += g[yi] * rbs
            bsum += b[yi] * rbs
            if (i > 0) {
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
            } else {
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
            }
            if (i < hm) {
                yp += w
            }
            i++
        }
        yi = x
        stackpointer = radius
        y = 0
        while (y < h) {

            // Preserve alpha channel: ( 0xff000000 & pix[yi] )
            pix[yi] = -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]
            rsum -= routsum
            gsum -= goutsum
            bsum -= boutsum
            stackstart = stackpointer - radius + div
            sir = stack[stackstart % div]
            routsum -= sir[0]
            goutsum -= sir[1]
            boutsum -= sir[2]
            if (x == 0) {
                vmin[y] = min(y + r1, hm) * w
            }
            p = x + vmin[y]
            sir[0] = r[p]
            sir[1] = g[p]
            sir[2] = b[p]
            rinsum += sir[0]
            ginsum += sir[1]
            binsum += sir[2]
            rsum += rinsum
            gsum += ginsum
            bsum += binsum
            stackpointer = (stackpointer + 1) % div
            sir = stack[stackpointer]
            routsum += sir[0]
            goutsum += sir[1]
            boutsum += sir[2]
            rinsum -= sir[0]
            ginsum -= sir[1]
            binsum -= sir[2]
            yi += w
            y++
        }
        x++
    }
    bitmap?.setPixels(pix, 0, w, 0, 0, w, h)
    return bitmap
}

fun RemoteViews.loadImageBlurred(context: Context, viewId: Int, link: String) {
    val errorRes = if (link.isEmpty()) R.drawable.empty_profile_picture else R.drawable.image_error

    // Load low-resolution image
    Glide.with(context)
        .asBitmap()
        .load(link)
        .apply(
            RequestOptions()
                .placeholder(R.drawable.loading_image)
                .error(errorRes)
        )
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                // Blur the image
                val blurryBitmap = resource.fastBlur()
                // Set the blurred image to the RemoteViews
                setImageViewBitmap(viewId, blurryBitmap)
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })
}


fun ImageView.loadImageWithBlurryPlaceholder(
    thumbnailUrl: String,
    originalUrl: String
) {
    Glide.with(context)
        .setDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.image_error)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
        )
        .asBitmap()
        .load(thumbnailUrl)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val blurryBitmap = resource.fastBlur()
                val blurryDrawable = BitmapDrawable(resources, blurryBitmap)

                Glide.with(context)
                    .load(originalUrl)
                    .placeholder(blurryDrawable)
                    .into(this@loadImageWithBlurryPlaceholder)
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })
}

fun ImageView.loadImage(link: String) {
    val errorRes = if (link.isEmpty()) R.drawable.empty_profile_picture else R.drawable.image_error
    Glide.with(this.context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.loading_image)
            .error(errorRes)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    ).load(link).into(this)
}

fun ImageView.loadImageBlurred(link: String) {
    val errorRes = if (link.isEmpty()) R.drawable.empty_profile_picture else R.drawable.image_error
    Glide.with(this.context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.loading_image)
            .error(errorRes)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    ).asBitmap().load(link).into(object : CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            val blurryBitmap = resource.fastBlur()
            val blurryDrawable = BitmapDrawable(resources, blurryBitmap)

            this@loadImageBlurred.setImageDrawable(blurryDrawable)
        }

        override fun onLoadCleared(placeholder: Drawable?) {}
    })
}

fun Media.loadMediaThumbs(imageView: ImageView) {
    if (isVideo.not()) {       // image item
        thumb?.let {
            imageView.loadImageWithBlurryPlaceholder(
                it.lowResolution,
                it.fullResolution
            )
        }
    } else {                                // video item
        thumb?.lowResolution?.let { imageView.loadImage(it) }
    }
}