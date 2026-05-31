package com.synfusion.pipelistpro.core.export

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.synfusion.pipelistpro.data.models.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ImageExportManager {
    private const val WIDTH = 1080

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
}
