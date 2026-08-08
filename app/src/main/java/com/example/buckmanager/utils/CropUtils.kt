package com.example.buckmanager.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.media.ExifInterface
import java.io.File
import java.io.FileOutputStream

fun cropImageBitmap(
    context: Context,
    imageUri: String,
    containerWidth: Float,
    containerHeight: Float,
    rectOffsetX: Float,
    rectOffsetY: Float,
    rectWidth: Float,
    rectHeight: Float,
    rotation: Float
): String? {
    try {
        val uri = Uri.parse(imageUri)
        val inputStream = if (imageUri.startsWith("http")) {
            java.net.URL(imageUri).openStream()
        } else {
            context.contentResolver.openInputStream(uri)
        } ?: return null
        
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        
        if (originalBitmap == null) return null

        // Get Exif rotation
        val exifInputStream = if (imageUri.startsWith("http")) {
            java.net.URL(imageUri).openStream()
        } else {
            context.contentResolver.openInputStream(uri)
        }
        var exifRotation = 0f
        if (exifInputStream != null) {
            val exif = ExifInterface(exifInputStream)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            exifRotation = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
            exifInputStream.close()
        }

        val matrix = Matrix()
        matrix.postRotate(exifRotation + rotation)

        val rotatedBitmap = Bitmap.createBitmap(
            originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true
        )

        // Calculate image bounds in container (ContentScale.Fit)
        val imageAspect = rotatedBitmap.width.toFloat() / rotatedBitmap.height.toFloat()
        val containerAspect = containerWidth / containerHeight

        var drawWidth = containerWidth
        var drawHeight = containerHeight
        var drawX = 0f
        var drawY = 0f

        if (imageAspect > containerAspect) {
            // Image is wider than container
            drawHeight = containerWidth / imageAspect
            drawY = (containerHeight - drawHeight) / 2f
        } else {
            // Image is taller than container
            drawWidth = containerHeight * imageAspect
            drawX = (containerWidth - drawWidth) / 2f
        }

        // Map crop rect from container coords to image coords
        val cropX = ((rectOffsetX - drawX) / drawWidth) * rotatedBitmap.width
        val cropY = ((rectOffsetY - drawY) / drawHeight) * rotatedBitmap.height
        val cropW = (rectWidth / drawWidth) * rotatedBitmap.width
        val cropH = (rectHeight / drawHeight) * rotatedBitmap.height

        val finalX = cropX.toInt().coerceIn(0, rotatedBitmap.width - 1)
        val finalY = cropY.toInt().coerceIn(0, rotatedBitmap.height - 1)
        val finalW = cropW.toInt().coerceIn(1, rotatedBitmap.width - finalX)
        val finalH = cropH.toInt().coerceIn(1, rotatedBitmap.height - finalY)

        val finalBitmap = Bitmap.createBitmap(rotatedBitmap, finalX, finalY, finalW, finalH)

        val file = File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
        val out = FileOutputStream(file)
        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        out.flush()
        out.close()

        return Uri.fromFile(file).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}
