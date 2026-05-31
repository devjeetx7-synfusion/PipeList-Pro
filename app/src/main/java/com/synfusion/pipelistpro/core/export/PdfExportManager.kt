package com.synfusion.pipelistpro.core.export

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.synfusion.pipelistpro.data.models.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

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
}
