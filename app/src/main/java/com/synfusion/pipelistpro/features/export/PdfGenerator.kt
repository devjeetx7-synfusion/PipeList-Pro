package com.synfusion.pipelistpro.features.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.synfusion.pipelistpro.data.models.Project
import com.synfusion.pipelistpro.data.models.CartItem
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 50f
    private const val CONTENT_BOTTOM_LIMIT = 780f

    fun generateProjectPdf(context: Context, project: Project): File? {
        val pdfDocument = PdfDocument()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        var pageCount = 1
        var page = startNewPage(pdfDocument, pageCount)
        var canvas = page.canvas

        drawHeader(canvas, project, paint)

        var y = 140f

        val groupedItems = project.items.groupBy { it.category }

        groupedItems.forEach { (category, items) ->
            if (y + 60f > CONTENT_BOTTOM_LIMIT) {
                drawFooter(canvas, paint)
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
                    drawFooter(canvas, paint)
                    pdfDocument.finishPage(page)
                    pageCount++
                    page = startNewPage(pdfDocument, pageCount)
                    canvas = page.canvas
                    y = MARGIN + 40f
                }

                drawItemRow(canvas, item, index + 1, y, paint)
                y += 25f
            }
            y += 10f
        }

        drawFooter(canvas, paint)
        pdfDocument.finishPage(page)

        val fileName = "PipeList_${project.projectName.replace(" ", "_")}.pdf"
        val file = File(context.cacheDir, fileName)
        return try {
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
        paint.textSize = 24f
        paint.isFakeBoldText = true
        paint.color = Color.BLACK
        canvas.drawText("PipeList Pro Material List", MARGIN, y, paint)

        y += 30f
        paint.textSize = 12f
        paint.isFakeBoldText = false
        paint.color = Color.GRAY
        canvas.drawText("Project: ${project.projectName}", MARGIN, y, paint)

        y += 20f
        canvas.drawText("Date: ${project.date}", MARGIN, y, paint)

        y += 15f
        paint.strokeWidth = 1.5f
        paint.color = Color.BLACK
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, paint)
    }

    private fun drawItemRow(canvas: Canvas, item: CartItem, index: Int, y: Float, paint: Paint) {
        paint.textSize = 13f
        paint.isFakeBoldText = false
        paint.color = Color.BLACK

        val sizeStr = if (item.size != "Standard" && item.size.isNotEmpty()) "${item.size} " else ""
        val ftStr = if (item.ft != null) " (${item.ft} ft) " else ""

        val itemText = "$index. $sizeStr${item.name}$ftStr"
        val quantityText = "${item.quantity} ${item.unit}"

        canvas.drawText(itemText, MARGIN + 10f, y, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(quantityText, PAGE_WIDTH - MARGIN, y, paint)
        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawFooter(canvas: Canvas, paint: Paint) {
        paint.textSize = 10f
        paint.isFakeBoldText = false
        paint.color = Color.GRAY
        paint.textAlign = Paint.Align.CENTER

        val footerY = PAGE_HEIGHT - 40f
        canvas.drawText("From PipeList Pro", PAGE_WIDTH / 2f, footerY, paint)

        paint.textAlign = Paint.Align.LEFT
    }
}
