package com.simats.echodiary.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object PhotoStorageHelper {

    private const val PHOTOS_DIR = "diary_photos"

    /**
     * Persists an image from a content/file URI into internal app sandboxed storage.
     * Guaranteed unique filename to prevent overwriting when multiple photos are imported.
     */
    fun savePhotoToInternalStorage(context: Context, uri: Uri): String {
        return try {
            val photosDir = File(context.filesDir, PHOTOS_DIR)
            if (!photosDir.exists()) {
                photosDir.mkdirs()
            }

            val uniqueSuffix = UUID.randomUUID().toString().replace("-", "").take(8)
            val destinationFile = File(photosDir, "photo_${System.currentTimeMillis()}_$uniqueSuffix.jpg")

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                    output.flush()
                }
            }

            if (destinationFile.exists() && destinationFile.length() > 0) {
                destinationFile.absolutePath
            } else {
                uri.toString()
            }
        } catch (_: Throwable) {
            uri.toString()
        }
    }

    /**
     * Persists multiple photos into internal app sandboxed storage.
     */
    fun savePhotosToInternalStorage(context: Context, uris: List<Uri>): List<String> {
        return uris.map { savePhotoToInternalStorage(context, it) }
    }

    /**
     * Safely decodes a photo with inSampleSize downsampling to prevent OutOfMemoryError.
     */
    fun decodeSampledBitmap(
        context: Context,
        pathOrUri: String?,
        reqWidth: Int = 1024,
        reqHeight: Int = 1024
    ): ImageBitmap? {
        val bitmap = decodeAndroidBitmap(context, pathOrUri, reqWidth, reqHeight)
        return bitmap?.asImageBitmap()
    }

    /**
     * Safely decodes a photo into an Android Bitmap with downsampling and Throwable safety.
     */
    fun decodeAndroidBitmap(
        context: Context,
        pathOrUri: String?,
        reqWidth: Int = 1024,
        reqHeight: Int = 1024
    ): Bitmap? {
        if (pathOrUri.isNullOrBlank()) return null

        return try {
            val file = File(pathOrUri)
            if (file.exists() && file.isFile) {
                decodeFileWithSample(file.absolutePath, reqWidth, reqHeight)
            } else {
                val uri = Uri.parse(pathOrUri)
                if (uri.scheme == "file") {
                    val filePath = uri.path
                    if (filePath != null && File(filePath).exists()) {
                        decodeFileWithSample(filePath, reqWidth, reqHeight)
                    } else {
                        decodeStreamWithSample(context, uri, reqWidth, reqHeight)
                    }
                } else {
                    decodeStreamWithSample(context, uri, reqWidth, reqHeight)
                }
            }
        } catch (_: Throwable) {
            null
        }
    }

    private fun decodeFileWithSample(path: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(path, options)

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        options.inPreferredConfig = Bitmap.Config.RGB_565

        return BitmapFactory.decodeFile(path, options)
    }

    private fun decodeStreamWithSample(context: Context, uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        var openStream: InputStream? = null
        try {
            openStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(openStream, null, options)
        } finally {
            try { openStream?.close() } catch (_: Throwable) {}
        }

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        options.inPreferredConfig = Bitmap.Config.RGB_565

        return context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize.coerceAtLeast(1)
    }
}
