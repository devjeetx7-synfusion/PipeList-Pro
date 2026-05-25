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

    fun shareProjectAsImage(context: Context, project: Project, view: View) {
        val bitmap = captureView(view)
        val file = File(context.cacheDir, "PipeList_${project.id}.png")
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

    private fun captureView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }
}
