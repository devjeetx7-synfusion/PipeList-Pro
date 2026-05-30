package com.synfusion.pipelistpro.core.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.synfusion.pipelistpro.data.models.Project
import com.synfusion.pipelistpro.data.models.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfExportManager {

    private const val PAGE_WIDTH = 595 // A4 width in points
    private const val PAGE_HEIGHT = 842 // A4 height in points
    private const val MARGIN = 40f
    private const val CONTENT_BOTTOM_LIMIT = 780f

    suspend fun generatePdf(context: Context, project: Project): File? = withContext(Dispatchers.IO) {
        val pdfDocument = PdfDocument()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        try {
            var pageCount = 1
            var page = startNewPage(pdfDocument, pageCount)
            var canvas = page.canvas

            drawHeader(canvas, project, paint)

            var y = 150f
            val groupedItems = project.items.groupBy { it.category }

            groupedItems.forEach { (category, items) ->
                // Check if category header fits
                if (y + 60f > CONTENT_BOTTOM_LIMIT) {
                    drawFooter(canvas, paint, pageCount)
                    pdfDocument.finishPage(page)
                    pageCount++
                    page = startNewPage(pdfDocument, pageCount)
                    canvas = page.canvas
                    y = MARGIN + 40f
                }

                // Category Header
                y += 20f
                paint.textSize = 14f
                paint.isFakeBoldText = true
                paint.color = Color.DKGRAY
                canvas.drawText(category.uppercase(), MARGIN, y, paint)

                y += 5f
                paint.strokeWidth = 0.5f
                paint.color = Color.LTGRAY
                canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, paint)
                y += 25f

                items.forEachIndexed { index, item ->
                    if (y + 30f > CONTENT_BOTTOM_LIMIT) {
                        drawFooter(canvas, paint, pageCount)
                        pdfDocument.finishPage(page)
                        pageCount++
                        page = startNewPage(pdfDocument, pageCount)
                        canvas = page.canvas

                        // Repeat category header on new page
                        y = MARGIN + 40f
                        paint.textSize = 10f
                        paint.isFakeBoldText = true
                        paint.color = Color.GRAY
                        canvas.drawText("$category (continued)", MARGIN, y, paint)
                        y += 25f
                    }

                    drawItemRow(canvas, item, index + 1, y, paint)
                    y += 25f
                }
                y += 10f
            }

            drawFooter(canvas, paint, pageCount)
            pdfDocument.finishPage(page)

            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val fileName = "PipeList_${project.projectName.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
            val file = File(exportDir, fileName)

            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.close()
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    private fun startNewPage(pdfDocument: PdfDocument, pageNumber: Int): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        val page = pdfDocument.startPage(pageInfo)
        page.canvas.drawColor(Color.WHITE)
        return page
    }

    private fun drawHeader(canvas: Canvas, project: Project, paint: Paint) {
        var y = 70f
        paint.textSize = 22f
        paint.isFakeBoldText = true
        paint.color = Color.BLACK
        canvas.drawText("PipeList Pro", MARGIN, y, paint)

        paint.textSize = 12f
        paint.isFakeBoldText = false
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()), PAGE_WIDTH - MARGIN, y - 5f, paint)
        paint.textAlign = Paint.Align.LEFT

        y += 35f
        paint.textSize = 16f
        paint.isFakeBoldText = true
        canvas.drawText(project.projectName, MARGIN, y, paint)

        if (project.clientName.isNotEmpty() || project.location.isNotEmpty()) {
            y += 20f
            paint.textSize = 11f
            paint.isFakeBoldText = false
            paint.color = Color.GRAY
            val clientInfo = listOfNotNull(
                project.clientName.takeIf { it.isNotEmpty() },
                project.location.takeIf { it.isNotEmpty() }
            ).joinToString(" | ")
            canvas.drawText(clientInfo, MARGIN, y, paint)
        }

        y += 15f
        paint.strokeWidth = 1.5f
        paint.color = Color.BLACK
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, paint)
    }

    private fun drawItemRow(canvas: Canvas, item: CartItem, index: Int, y: Float, paint: Paint) {
        paint.textSize = 12f
        paint.isFakeBoldText = false
        paint.color = Color.BLACK

        val sizeStr = if (item.size != "Standard" && item.size.isNotEmpty()) "${item.size} " else ""
        val ftStr = if (item.ft != null) " (${item.ft} ft) " else ""

        val nameText = "$index. $sizeStr${item.name}$ftStr"
        val qtyText = "${item.quantity} ${item.unit}"

        // Ellipsize long text
        val maxWidth = PAGE_WIDTH - MARGIN - 120f
        val ellipsizedName = if (paint.measureText(nameText) > maxWidth) {
            var text = nameText
            while (paint.measureText("$text...") > maxWidth && text.isNotEmpty()) {
                text = text.dropLast(1)
            }
            "$text..."
        } else {
            nameText
        }

        canvas.drawText(ellipsizedName, MARGIN + 10f, y, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(qtyText, PAGE_WIDTH - MARGIN, y, paint)
        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawFooter(canvas: Canvas, paint: Paint, pageNumber: Int) {
        paint.textSize = 10f
        paint.isFakeBoldText = false
        paint.color = Color.GRAY
        paint.textAlign = Paint.Align.CENTER

        val footerY = PAGE_HEIGHT - 30f
        canvas.drawText("Page $pageNumber | Generated by PipeList Pro", PAGE_WIDTH / 2f, footerY, paint)

        paint.textAlign = Paint.Align.LEFT
    }
}
