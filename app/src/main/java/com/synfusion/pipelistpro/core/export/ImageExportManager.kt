package com.synfusion.pipelistpro.core.export

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.synfusion.pipelistpro.data.models.CartItem
import com.synfusion.pipelistpro.data.models.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageExportManager {
    private const val WIDTH = 1080
    private const val MARGIN = 60f
    private const val MAX_HEIGHT = 12000

    suspend fun generateImage(context: Context, project: Project): File? = withContext(Dispatchers.IO) {
        if (project.items.isEmpty()) return@withContext null
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val style = ReceiptExportRenderer.imageStyle

        try {
            val finalHeight = ReceiptExportRenderer.measureExportHeight(project, paint, style).toInt().coerceAtLeast(260)
            val bitmap = Bitmap.createBitmap(WIDTH, finalHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(ReceiptExportRenderer.imageOutsideBackground())

            paint.color = Color.WHITE
            canvas.drawRoundRect(RectF(24f, 20f, WIDTH - 24f, finalHeight - 20f), 16f, 16f, paint)

            var y = ReceiptExportRenderer.drawHeader(canvas, paint, style, project)
            ReceiptExportRenderer.groupedItems(project).forEach { (category, items) ->
                y = ReceiptExportRenderer.drawCategoryHeader(canvas, paint, style, category, y)
                items.forEachIndexed { index, item ->
                    y = ReceiptExportRenderer.drawItemRow(canvas, paint, style, item, index + 1, y)
                }
            }
            ReceiptExportRenderer.drawFooter(canvas, paint, style, y)

            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val file = File(exportDir, "PipeList_${ReceiptExportRenderer.safeFileName(project.projectName)}_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            bitmap.recycle()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun drawImageRow(canvas: Canvas, paint: Paint, item: CartItem, index: Int, y: Float): Float {
        val name = buildString {
            append(index).append(". ")
            if (item.size.isNotBlank() && item.size != "Standard") append(item.size).append(' ')
            append(item.name)
        }
        val lines = wrapText(paint, name, 690f, 26f).take(3)
        val rowHeight = (lines.size * 34f + 34f).coerceAtLeast(72f)
        paint.color = Color.WHITE
        canvas.drawRoundRect(RectF(MARGIN, y - 34f, WIDTH - MARGIN, y - 34f + rowHeight), 18f, 18f, paint)
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 26f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(17, 24, 39)
        var lineY = y
        lines.forEach { line ->
            canvas.drawText(line, MARGIN + 24f, lineY, paint)
            lineY += 34f
        }
        paint.textSize = 22f
        paint.color = Color.rgb(100, 116, 139)
        val meta = item.ft?.let { formatNumber(it) + " ft" }.orEmpty()
        if (meta.isNotBlank()) canvas.drawText(meta, MARGIN + 24f, lineY, paint)
        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 28f
        paint.isFakeBoldText = true
        paint.color = Color.rgb(0, 87, 217)
        canvas.drawText("${item.quantity} ${item.unit}", WIDTH - MARGIN - 24f, y, paint)
        return y - 34f + rowHeight + 18f
    }

    private fun estimateRowHeight(paint: Paint, item: CartItem): Float {
        val lines = wrapText(paint, item.name, 690f, 26f).size.coerceAtMost(3)
        return (lines * 34f + 52f).coerceAtLeast(90f)
    }

    private fun wrapText(paint: Paint, text: String, maxWidth: Float, textSize: Float): List<String> {
        paint.textSize = textSize
        val lines = mutableListOf<String>()
        var current = ""
        text.split(Regex("\\s+")).filter { it.isNotBlank() }.forEach { word ->
            val candidate = if (current.isBlank()) word else "$current $word"
            if (paint.measureText(candidate) <= maxWidth) current = candidate else {
                if (current.isNotBlank()) lines.add(current)
                current = word
            }
        }
        if (current.isNotBlank()) lines.add(current)
        return lines.ifEmpty { listOf("") }
    }

    private fun formatNumber(value: Double): String = if (value % 1.0 == 0.0) value.toInt().toString() else String.format(Locale.US, "%.2f", value).trimEnd('0').trimEnd('.')
    private fun safeFileName(name: String): String = name.ifBlank { "Material_List" }.replace(Regex("[^A-Za-z0-9_-]+"), "_").take(48)
}
