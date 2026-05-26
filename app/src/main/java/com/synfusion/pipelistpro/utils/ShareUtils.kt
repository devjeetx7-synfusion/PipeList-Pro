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

    fun shareProjectAsText(context: Context, project: Project) {
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

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text.toString())
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
        val margin = 50f
        val width = 800

        // Calculate dynamic height
        var estimatedHeight = 200f // Header
        val groupedItems = project.items.groupBy { it.category }
        groupedItems.forEach { (category, items) ->
            estimatedHeight += 60f // Category header
            estimatedHeight += items.size * 35f // Items
            estimatedHeight += 20f // Spacing
        }
        estimatedHeight += 100f // Footer

        val bitmap = Bitmap.createBitmap(width, estimatedHeight.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        var y = 70f

        // Header
        paint.textSize = 32f
        paint.isFakeBoldText = true
        paint.color = Color.BLACK
        canvas.drawText("PipeList Pro Material List", margin, y, paint)

        y += 40f
        paint.textSize = 18f
        paint.isFakeBoldText = false
        paint.color = Color.GRAY
        canvas.drawText("Project: ${project.projectName}", margin, y, paint)

        y += 25f
        canvas.drawText("Date: ${project.date}", margin, y, paint)

        y += 20f
        paint.strokeWidth = 2f
        paint.color = Color.BLACK
        canvas.drawLine(margin, y, width - margin, y, paint)

        y += 40f

        // Content
        groupedItems.forEach { (category, items) ->
            y += 10f
            paint.textSize = 20f
            paint.isFakeBoldText = true
            paint.color = Color.DKGRAY
            canvas.drawText(category.uppercase(), margin, y, paint)

            y += 8f
            paint.strokeWidth = 1f
            paint.color = Color.LTGRAY
            canvas.drawLine(margin, y, width - margin, y, paint)
            y += 30f

            items.forEachIndexed { index, item ->
                paint.textSize = 18f
                paint.isFakeBoldText = false
                paint.color = Color.BLACK
                val sizeStr = if (item.size != "Standard" && item.size.isNotEmpty()) "${item.size} " else ""
                val itemText = "${index + 1}. $sizeStr${item.materialName} — ${item.quantity} ${item.unit}"
                canvas.drawText(itemText, margin + 20f, y, paint)
                y += 35f
            }
            y += 15f
        }

        // Footer
        y = estimatedHeight - 40f
        paint.textSize = 14f
        paint.color = Color.GRAY
        paint.textAlign = android.graphics.Paint.Align.CENTER
        canvas.drawText("From PipeList Pro", width / 2f, y, paint)

        return bitmap
    }
}
