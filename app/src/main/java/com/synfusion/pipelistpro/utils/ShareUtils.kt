package com.synfusion.pipelistpro.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import androidx.core.content.FileProvider
import com.synfusion.pipelistpro.model.Project
import java.io.File
import java.io.FileOutputStream

object ShareUtils {

    fun formatProjectData(project: Project): String {
        val text = StringBuilder()
        text.append("PipeList Pro Material List\n")
        text.append("Project: ${project.projectName}\n")
        text.append("Date: ${project.date}\n")
        text.append("----------------------------\n\n")

        val groupedItems = project.items.groupBy { it.category }
        groupedItems.forEach { (category, items) ->
            text.append("${category.uppercase()}\n")
            items.forEachIndexed { index, item ->
                val sizeStr = if (item.size != "Standard" && item.size.isNotEmpty()) "${item.size} " else ""
                text.append("${index + 1}. $sizeStr${item.materialName} — ${item.quantity} ${item.unit}\n")
            }
            text.append("\n")
        }

        text.append("----------------------------\n")
        text.append("From PipeList Pro")
        return text.toString()
    }

    fun shareProjectAsText(context: Context, project: Project) {
        val formattedText = formatProjectData(project)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, formattedText)
        context.startActivity(Intent.createChooser(intent, "Share via"))
    }

    fun sharePdfFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intent, "Share PDF"))
    }

    fun shareProjectAsImage(context: Context, project: Project) {
        val bitmap = generateProjectImage(project)
        val file = File(context.cacheDir, "PipeList_${project.projectName.replace(" ", "_")}.png")
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/png"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(Intent.createChooser(intent, "Share Image"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun generateProjectImage(project: Project): Bitmap {
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        val margin = 60f
        val width = 1000

        val groupedItems = project.items.groupBy { it.category }

        // Calculate dynamic height based on content
        var yPos = 240f // Header offset
        groupedItems.forEach { (_, items) ->
            yPos += 80f // Category header height
            yPos += items.size * 50f // Rows
            yPos += 30f // Bottom padding for category
        }
        yPos += 120f // Final footer offset

        val bitmap = Bitmap.createBitmap(width, yPos.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        var y = 90f

        // Header - Professional Notes Style
        paint.textSize = 42f
        paint.isFakeBoldText = true
        paint.color = Color.BLACK
        canvas.drawText("PipeList Pro Material List", margin, y, paint)

        y += 50f
        paint.textSize = 22f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(60, 60, 60)
        canvas.drawText("Project: ${project.projectName}", margin, y, paint)

        y += 35f
        canvas.drawText("Date: ${project.date}", margin, y, paint)

        y += 30f
        paint.strokeWidth = 3f
        paint.color = Color.BLACK
        canvas.drawLine(margin, y, width - margin, y, paint)

        y += 70f

        // Content rendering
        groupedItems.forEach { (category, items) ->
            paint.textSize = 28f
            paint.isFakeBoldText = true
            paint.color = Color.rgb(40, 40, 40)
            canvas.drawText(category.uppercase(), margin, y, paint)

            y += 15f
            paint.strokeWidth = 1.5f
            paint.color = Color.rgb(200, 200, 200)
            canvas.drawLine(margin, y, width - margin, y, paint)
            y += 50f

            items.forEachIndexed { index, item ->
                paint.textSize = 24f
                paint.isFakeBoldText = false
                paint.color = Color.BLACK
                val sizeStr = if (item.size != "Standard" && item.size.isNotEmpty()) "${item.size} " else ""
                val itemText = "${index + 1}. $sizeStr${item.materialName}"
                val quantityText = "${item.quantity} ${item.unit}"

                canvas.drawText(itemText, margin + 30f, y, paint)

                // Draw quantity right-aligned
                paint.textAlign = android.graphics.Paint.Align.RIGHT
                canvas.drawText(quantityText, width - margin - 30f, y, paint)
                paint.textAlign = android.graphics.Paint.Align.LEFT

                y += 50f
            }
            y += 20f
        }

        // Footer branding
        y = yPos - 60f
        paint.textSize = 18f
        paint.color = Color.GRAY
        paint.textAlign = android.graphics.Paint.Align.CENTER
        canvas.drawText("Professional materials list generated via PipeList Pro", width / 2f, y, paint)

        return bitmap
    }
}
