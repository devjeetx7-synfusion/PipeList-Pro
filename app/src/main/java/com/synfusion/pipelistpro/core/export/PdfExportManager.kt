package com.synfusion.pipelistpro.core.export

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import com.synfusion.pipelistpro.data.models.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExportManager {
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842

    suspend fun generatePdf(context: Context, project: Project): File? = withContext(Dispatchers.IO) {
        if (project.items.isEmpty()) return@withContext null
        val pdf = PdfDocument()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val style = ReceiptExportRenderer.pdfStyle
        val contentBottom = PAGE_HEIGHT - style.bottomPadding

        try {
            var pageNumber = 1
            var page = newPage(pdf, pageNumber)
            var canvas = page.canvas
            var y = ReceiptExportRenderer.drawHeader(canvas, paint, style, project)
            var footerDrawn = false

            fun finishCurrentPage(contentReachedBottom: Boolean = false) {
                if (!footerDrawn) {
                    val footerY = if (contentReachedBottom) {
                        PAGE_HEIGHT - style.bottomPadding - style.footerHeight - style.footerTopGap
                    } else {
                        y
                    }
                    ReceiptExportRenderer.drawFooter(canvas, paint, style, footerY)
                    footerDrawn = true
                }
                pdf.finishPage(page)
            }

            fun startNextPage() {
                finishCurrentPage(contentReachedBottom = true)
                pageNumber += 1
                page = newPage(pdf, pageNumber)
                canvas = page.canvas
                y = ReceiptExportRenderer.drawHeader(canvas, paint, style, project)
                footerDrawn = false
            }

            ReceiptExportRenderer.groupedItems(project).forEach { (category, items) ->
                val firstRowHeight = items.firstOrNull()?.let { ReceiptExportRenderer.measureItemRowHeight(paint, style, it, 1) } ?: 0f
                val categoryBlockHeight = style.categoryTopGap + style.categoryHeight + style.categoryBottomGap + firstRowHeight
                if (y + categoryBlockHeight + style.footerTopGap + style.footerHeight > contentBottom) {
                    startNextPage()
                }

                y = ReceiptExportRenderer.drawCategoryHeader(canvas, paint, style, category, y)
                items.forEachIndexed { index, item ->
                    val rowHeight = ReceiptExportRenderer.measureItemRowHeight(paint, style, item, index + 1)
                    if (y + rowHeight + style.footerTopGap + style.footerHeight > contentBottom) {
                        startNextPage()
                        y = ReceiptExportRenderer.drawCategoryHeader(canvas, paint, style, category, y)
                    }
                    y = ReceiptExportRenderer.drawItemRow(canvas, paint, style, item, index + 1, y)
                }
            }

            finishCurrentPage(contentReachedBottom = false)

            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val file = File(exportDir, "PipeList_${ReceiptExportRenderer.safeFileName(project.projectName)}_${System.currentTimeMillis()}.pdf")
            FileOutputStream(file).use { pdf.writeTo(it) }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            pdf.close()
        }
    }

    private fun newPage(pdf: PdfDocument, pageNumber: Int): PdfDocument.Page {
        val page = pdf.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create())
        page.canvas.drawColor(Color.WHITE)
        return page
    }

    private fun drawHeader(canvas: Canvas, paint: Paint, project: Project): Float {
        var y = 56f
        paint.textAlign = Paint.Align.LEFT
        paint.color = Color.rgb(0, 87, 217)
        paint.textSize = 23f
        paint.isFakeBoldText = true
        canvas.drawText("PipeList Pro", MARGIN, y, paint)

        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 10.5f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(92, 104, 120)
        canvas.drawText(SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date()), PAGE_WIDTH - MARGIN, y - 3f, paint)
        paint.textAlign = Paint.Align.LEFT

        y += 32f
        paint.color = Color.rgb(17, 24, 39)
        paint.textSize = 16f
        paint.isFakeBoldText = true
        wrapText(paint, project.projectName.ifBlank { "Material List" }, PAGE_WIDTH - MARGIN * 2, 16f).forEach { line ->
            canvas.drawText(line, MARGIN, y, paint)
            y += 20f
        }

        paint.textSize = 10.5f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(92, 104, 120)
        canvas.drawText("List date: ${project.date.ifBlank { "Not set" }}", MARGIN, y, paint)
        y += 16f
        if (project.notes.isNotBlank()) {
            wrapText(paint, "Notes: ${project.notes}", PAGE_WIDTH - MARGIN * 2, 10.5f).take(3).forEach { line ->
                canvas.drawText(line, MARGIN, y, paint)
                y += 14f
            }
        }
        drawDivider(canvas, paint, y + 4f)
        return y + 18f
    }

    private fun drawCompactHeader(canvas: Canvas, paint: Paint, project: Project): Float {
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 13f
        paint.isFakeBoldText = true
        paint.color = Color.rgb(0, 87, 217)
        canvas.drawText("PipeList Pro", MARGIN, 46f, paint)
        paint.textSize = 10f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(92, 104, 120)
        canvas.drawText(project.projectName.ifBlank { "Material List" }, 135f, 46f, paint)
        drawDivider(canvas, paint, 58f)
        return 76f
    }

    private fun drawCategoryHeader(canvas: Canvas, paint: Paint, category: String, y: Float) {
        paint.textAlign = Paint.Align.LEFT
        paint.color = Color.rgb(235, 242, 255)
        canvas.drawRoundRect(RectF(MARGIN, y - 17f, PAGE_WIDTH - MARGIN, y + 8f), 10f, 10f, paint)
        paint.color = Color.rgb(0, 87, 217)
        paint.textSize = 12f
        paint.isFakeBoldText = true
        canvas.drawText(category.uppercase(Locale.getDefault()), MARGIN + 10f, y, paint)
    }

    private fun drawItemRow(canvas: Canvas, paint: Paint, nameLines: List<String>, item: CartItem, y: Float, rowHeight: Float): Float {
        val top = y - 14f
        paint.color = Color.rgb(248, 250, 252)
        canvas.drawRoundRect(RectF(MARGIN, top, PAGE_WIDTH - MARGIN, top + rowHeight - 4f), 8f, 8f, paint)

        paint.textSize = 11.5f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(17, 24, 39)
        var lineY = y
        nameLines.forEach { line ->
            canvas.drawText(line, MARGIN + 10f, lineY, paint)
            lineY += 15f
        }

        paint.textSize = 10.5f
        paint.color = Color.rgb(92, 104, 120)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(item.metaText(), PAGE_WIDTH - 104f, y, paint)
        paint.isFakeBoldText = true
        paint.color = Color.rgb(17, 24, 39)
        canvas.drawText("${item.quantity} ${item.unit}", PAGE_WIDTH - MARGIN - 8f, y, paint)
        paint.textAlign = Paint.Align.LEFT
        return top + rowHeight + 4f
    }

    private fun drawFooter(canvas: Canvas, paint: Paint, pageNumber: Int) {
        drawDivider(canvas, paint, PAGE_HEIGHT - 48f)
        paint.textSize = 9.5f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(107, 114, 128)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Page $pageNumber • Generated by PipeList Pro", PAGE_WIDTH / 2f, PAGE_HEIGHT - 28f, paint)
        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawDivider(canvas: Canvas, paint: Paint, y: Float) {
        paint.strokeWidth = 1f
        paint.color = Color.rgb(229, 231, 235)
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, paint)
    }

    private fun CartItem.displayName(index: Int): String {
        val sizeText = size.takeIf { it.isNotBlank() && it != "Standard" }?.let { "$it " }.orEmpty()
        return "$index. $sizeText$name"
    }

    private fun CartItem.metaText(): String {
        val sizeText = size.takeIf { it.isNotBlank() && it != "Standard" }.orEmpty()
        val ftText = ft?.let { formatNumber(it) + " ft" }.orEmpty()
        return listOf(sizeText, ftText).filter { it.isNotBlank() }.joinToString(" • ").ifBlank { category }
    }

    private fun wrapText(paint: Paint, text: String, maxWidth: Float, textSize: Float): List<String> {
        paint.textSize = textSize
        val words = text.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (words.isEmpty()) return listOf("")
        val lines = mutableListOf<String>()
        var current = ""
        words.forEach { word ->
            val candidate = if (current.isBlank()) word else "$current $word"
            if (paint.measureText(candidate) <= maxWidth) current = candidate else {
                if (current.isNotBlank()) lines.add(current)
                current = word
            }
        }
        if (current.isNotBlank()) lines.add(current)
        return lines
    }

    private fun formatNumber(value: Double): String = if (value % 1.0 == 0.0) value.toInt().toString() else String.format(Locale.US, "%.2f", value).trimEnd('0').trimEnd('.')
    private fun safeFileName(name: String): String = name.ifBlank { "Material_List" }.replace(Regex("[^A-Za-z0-9_-]+"), "_").take(48)
}
