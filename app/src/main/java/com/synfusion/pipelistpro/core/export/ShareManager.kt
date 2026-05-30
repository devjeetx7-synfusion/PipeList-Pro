package com.synfusion.pipelistpro.core.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.synfusion.pipelistpro.data.models.Project
import java.io.File

object ShareManager {

    fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, title))
    }

    fun shareText(context: Context, project: Project) {
        val text = StringBuilder().apply {
            append("PipeList Pro - ${project.projectName}\n")
            append("Date: ${project.date}\n")
            append("----------------------------\n")

            project.items.groupBy { it.category }.forEach { (category, items) ->
                append("\n[${category.uppercase()}]\n")
                items.forEachIndexed { index, item ->
                    val sizeStr = if (item.size != "Standard" && item.size.isNotEmpty()) "${item.size} " else ""
                    val ftStr = if (item.ft != null) "(${item.ft} ft) " else ""
                    append("${index + 1}. $sizeStr${item.name} $ftStr- ${item.quantity} ${item.unit}\n")
                }
            }
            append("\n----------------------------\n")
            append("Generated via PipeList Pro")
        }.toString()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }

        context.startActivity(Intent.createChooser(intent, "Share List"))
    }
}
