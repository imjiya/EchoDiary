package com.simats.echodiary.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object MemoryImageShareHelper {

    fun shareMemoryAsImage(
        context: Context,
        title: String,
        dateRange: String,
        narrative: String,
        highlights: List<String> = emptyList(),
        photoUri: String? = null
    ) {
        try {
            val bitmap = createMemoryBitmap(context, title, dateRange, narrative, highlights, photoUri)
            val file = saveBitmapToCache(context, bitmap)
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(
                    Intent.EXTRA_TEXT,
                    "✨ $title\n📅 $dateRange\n\n$narrative\n\n— Generated with EchoDiary"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "Share Memory as Image").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to share memory image: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createMemoryBitmap(
        context: Context,
        title: String,
        dateRange: String,
        narrative: String,
        highlights: List<String>,
        photoUri: String?
    ): Bitmap {
        val width = 1080
        val padding = 70f
        val contentWidth = width - (padding * 2)

        // Load photo if available
        var attachedBitmap: Bitmap? = null
        if (!photoUri.isNullOrBlank()) {
            attachedBitmap = try {
                val f = File(photoUri)
                if (f.exists()) {
                    BitmapFactory.decodeFile(f.absolutePath)
                } else {
                    val u = Uri.parse(photoUri)
                    context.contentResolver.openInputStream(u)?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                }
            } catch (_: Exception) {
                null
            }
        }

        // Paints
        val brandPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4338CA")
            textSize = 28f
            isFakeBoldText = true
            letterSpacing = 0.12f
        }

        val datePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#64748B")
            textSize = 28f
        }

        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0F172A")
            textSize = 46f
            isFakeBoldText = true
        }

        val narrativePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#1E293B")
            textSize = 32f
        }

        val highlightTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#475569")
            textSize = 26f
            isFakeBoldText = true
        }

        val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#94A3B8")
            textSize = 24f
        }

        val cleanTitle = title.ifBlank { "EchoDiary Memory Summary" }
        val titleLayout = StaticLayout.Builder.obtain(cleanTitle, 0, cleanTitle.length, titlePaint, contentWidth.toInt())
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(8f, 1f)
            .build()

        val narrativeCardPadding = 50f
        val narrativeContentWidth = contentWidth - (narrativeCardPadding * 2)
        val cleanNarrative = narrative.ifBlank { "Recorded moments and personal reflections captured in EchoDiary." }
        val narrativeLayout = StaticLayout.Builder.obtain(cleanNarrative, 0, cleanNarrative.length, narrativePaint, narrativeContentWidth.toInt())
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(10f, 1.15f)
            .build()

        var calculatedHeight = padding + 60f + 30f // Top padding & brand header
        calculatedHeight += 40f + 20f // Date range
        calculatedHeight += titleLayout.height + 40f // Title
        if (attachedBitmap != null) {
            calculatedHeight += 500f + 40f // Photo height + margin
        }
        calculatedHeight += narrativeLayout.height + (narrativeCardPadding * 2) + 40f // Narrative card

        if (highlights.isNotEmpty()) {
            calculatedHeight += 50f + 70f + 30f // Highlights label + chips
        }

        calculatedHeight += 80f + padding // Footer & bottom padding

        val height = calculatedHeight.toInt().coerceAtLeast(1300)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw outer background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#F8FAFC")
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Draw card background
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
        }
        val cardRect = RectF(28f, 28f, width - 28f, height - 28f)
        canvas.drawRoundRect(cardRect, 40f, 40f, cardPaint)

        // Draw card border
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#E2E8F0")
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawRoundRect(cardRect, 40f, 40f, borderPaint)

        var curY = padding + 30f

        // Brand header
        canvas.drawText("ECHO DIARY  ·  AI SOURCE MEMORY", padding, curY, brandPaint)
        curY += 45f

        // Date range
        canvas.drawText(dateRange.uppercase(), padding, curY, datePaint)
        curY += 45f

        // Title
        canvas.save()
        canvas.translate(padding, curY)
        titleLayout.draw(canvas)
        canvas.restore()
        curY += titleLayout.height + 40f

        // Photo (if present)
        if (attachedBitmap != null) {
            val photoHeight = 500f
            val photoRect = RectF(padding, curY, padding + contentWidth, curY + photoHeight)
            val clipPath = android.graphics.Path().apply {
                addRoundRect(photoRect, 30f, 30f, android.graphics.Path.Direction.CW)
            }
            canvas.save()
            canvas.clipPath(clipPath)

            // Center crop the bitmap
            val srcRect = Rect(0, 0, attachedBitmap.width, attachedBitmap.height)
            val srcAspect = attachedBitmap.width.toFloat() / attachedBitmap.height
            val dstAspect = contentWidth / photoHeight
            if (srcAspect > dstAspect) {
                val newWidth = (attachedBitmap.height * dstAspect).toInt()
                val offset = (attachedBitmap.width - newWidth) / 2
                srcRect.left = offset
                srcRect.right = offset + newWidth
            } else {
                val newHeight = (attachedBitmap.width / dstAspect).toInt()
                val offset = (attachedBitmap.height - newHeight) / 2
                srcRect.top = offset
                srcRect.bottom = offset + newHeight
            }
            canvas.drawBitmap(attachedBitmap, srcRect, photoRect, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
            canvas.restore()
            curY += photoHeight + 40f
        }

        // Narrative box
        val boxHeight = narrativeLayout.height + (narrativeCardPadding * 2)
        val boxRect = RectF(padding, curY, padding + contentWidth, curY + boxHeight)
        val narrativeBoxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#F1F5F9")
        }
        canvas.drawRoundRect(boxRect, 30f, 30f, narrativeBoxPaint)

        // Sparkle icon
        val sparklePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 36f
        }
        canvas.drawText("✨", padding + narrativeCardPadding, curY + narrativeCardPadding + 30f, sparklePaint)

        // Narrative text
        canvas.save()
        canvas.translate(padding + narrativeCardPadding, curY + narrativeCardPadding + 50f)
        narrativeLayout.draw(canvas)
        canvas.restore()
        curY += boxHeight + 40f

        // Highlights
        if (highlights.isNotEmpty()) {
            val hHeaderPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#0F172A")
                textSize = 30f
                isFakeBoldText = true
            }
            canvas.drawText("Highlights", padding, curY, hHeaderPaint)
            curY += 45f

            var curX = padding
            for (h in highlights) {
                val textWidth = highlightTextPaint.measureText(h)
                val chipWidth = textWidth + 40f
                if (curX + chipWidth > width - padding) {
                    curX = padding
                    curY += 70f
                }
                val chipRect = RectF(curX, curY - 35f, curX + chipWidth, curY + 20f)
                val chipBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#EEF2FF")
                }
                canvas.drawRoundRect(chipRect, 25f, 25f, chipBg)
                canvas.drawText(h, curX + 20f, curY, highlightTextPaint)
                curX += chipWidth + 20f
            }
            curY += 60f
        }

        // Footer brand
        curY = (height - padding).coerceAtLeast(curY + 40f)
        canvas.drawText("Generated locally with EchoDiary · Grounded in your entries", padding, curY, footerPaint)

        return bitmap
    }

    private fun saveBitmapToCache(context: Context, bitmap: Bitmap): File {
        val cacheDir = File(context.cacheDir, "shared_memories")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val file = File(cacheDir, "memory_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }
}

