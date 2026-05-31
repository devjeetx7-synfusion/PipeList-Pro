package com.synfusion.pipelistpro.core.export

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.synfusion.pipelistpro.data.models.CartItem
import com.synfusion.pipelistpro.data.models.Project
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

internal object ReceiptExportRenderer {
    private val categoryOrder = listOf(
        "UPVC", "PVC", "CPVC", "SWR", "PPR", "GI", "HDPE / MDPE", "Copper / Brass",
        "Valves", "Bathroom Fittings", "Sanitary Ware", "Water Tank & Tank Fittings",
        "Pump & Motor", "Drainage / Manhole / Rainwater", "MS Pipe & Fittings",
        "Stainless Steel / SS", "Borewell / Column Pipe", "Fire Fighting",
        "Gas Line", "Solar / Geyser Hot Water", "Irrigation / Garden",
        "Swimming Pool Plumbing", "Accessories / Clamps / Supports",
        "Consumables", "Plumbing Tools", "OTHER"
    )
    private val categoryLookup = categoryOrder.associateBy { it.uppercase(Locale.getDefault()) }

    val imageStyle = ExportStyle(
        width = 1080f,
        horizontalMargin = 54f,
        topPadding = 44f,
        bottomPadding = 38f,
        appNameTextSize = 42f,
        dateTextSize = 22f,
        categoryTextSize = 24f,
        itemTextSize = 24f,
        quantityTextSize = 24f,
        footerTextSize = 19f,
        headerLineGap = 30f,
        headerBottomGap = 20f,
        categoryTopGap = 12f,
        categoryHeight = 40f,
        categoryBottomGap = 4f,
        rowVerticalPadding = 7f,
        rowLineHeight = 29f,
        rowMinHeight = 42f,
        footerTopGap = 24f,
        footerHeight = 24f,
        rowNumberWidth = 48f,
        quantityWidth = 140f,
        categoryCornerRadius = 12f
    )

    val pdfStyle = ExportStyle(
        width = 595f,
        horizontalMargin = 34f,
        topPadding = 34f,
        bottomPadding = 28f,
        appNameTextSize = 18f,
        dateTextSize = 9.5f,
        categoryTextSize = 11.5f,
        itemTextSize = 11f,
        quantityTextSize = 11.5f,
        footerTextSize = 9f,
        headerLineGap = 13f,
        headerBottomGap = 8f,
        categoryTopGap = 6f,
        categoryHeight = 21f,
        categoryBottomGap = 3f,
        rowVerticalPadding = 4f,
        rowLineHeight = 13.5f,
        rowMinHeight = 24f,
        footerTopGap = 10f,
        footerHeight = 11f,
        rowNumberWidth = 26f,
        quantityWidth = 70f,
        categoryCornerRadius = 6f
    )

    fun measureExportHeight(project: Project, paint: Paint, style: ExportStyle): Float {
        var height = style.topPadding + headerHeight(style)
        groupedItems(project).forEach { (_, items) ->
            height += style.categoryTopGap + style.categoryHeight + style.categoryBottomGap
            items.forEachIndexed { index, item ->
                height += measureItemRowHeight(paint, style, item, index + 1)
            }
        }
        height += style.footerTopGap + style.footerHeight + style.bottomPadding
        return ceil(height)
    }

    fun drawHeader(canvas: Canvas, paint: Paint, style: ExportStyle, project: Project, y: Float = style.topPadding): Float {
        resetPaint(paint)
        paint.textAlign = Paint.Align.LEFT
        paint.color = PRIMARY_BLUE
        paint.textSize = style.appNameTextSize
        paint.isFakeBoldText = true
        canvas.drawText("PipeList Pro", style.left, y + style.appNameTextSize, paint)

        paint.textSize = style.dateTextSize
        paint.isFakeBoldText = false
        paint.color = TEXT_GREY
        canvas.drawText("Date: ${exportDate(project)}", style.left, y + style.appNameTextSize + style.headerLineGap, paint)
        return y + headerHeight(style)
    }

    fun drawCategoryHeader(canvas: Canvas, paint: Paint, style: ExportStyle, category: String, y: Float): Float {
        val top = y + style.categoryTopGap
        resetPaint(paint)
        paint.color = CATEGORY_BG
        canvas.drawRoundRect(RectF(style.left, top, style.right, top + style.categoryHeight), style.categoryCornerRadius, style.categoryCornerRadius, paint)

        paint.color = PRIMARY_BLUE
        paint.textSize = style.categoryTextSize
        paint.isFakeBoldText = true
        paint.textAlign = Paint.Align.LEFT
        val baseline = top + (style.categoryHeight - (paint.descent() + paint.ascent())) / 2f
        canvas.drawText(category, style.left + style.categoryTextLeftPadding, baseline, paint)
        return top + style.categoryHeight + style.categoryBottomGap
    }

    fun drawItemRow(canvas: Canvas, paint: Paint, style: ExportStyle, item: CartItem, index: Int, y: Float): Float {
        val rowHeight = measureItemRowHeight(paint, style, item, index)
        val itemText = itemDisplayName(item)
        val lines = wrapText(paint, itemText, style.itemTextWidth, style.itemTextSize).take(2)
        val numberText = "$index."
        val quantityText = "${item.quantity.coerceAtLeast(1)} ${item.unit.ifBlank { "pcs" }}"

        resetPaint(paint)
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = style.itemTextSize
        paint.isFakeBoldText = false
        paint.color = TEXT_DARK
        var baseline = y + style.rowVerticalPadding - paint.ascent()
        canvas.drawText(numberText, style.left + style.rowInnerPadding, baseline, paint)
        lines.forEachIndexed { lineIndex, line ->
            canvas.drawText(line, style.itemTextLeft, baseline + (lineIndex * style.rowLineHeight), paint)
        }

        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = style.quantityTextSize
        paint.isFakeBoldText = true
        paint.color = PRIMARY_BLUE
        canvas.drawText(quantityText, style.right - style.rowInnerPadding, baseline, paint)

        paint.strokeWidth = 1f
        paint.color = DIVIDER
        canvas.drawLine(style.left, y + rowHeight, style.right, y + rowHeight, paint)
        return y + rowHeight
    }

    fun drawFooter(canvas: Canvas, paint: Paint, style: ExportStyle, y: Float): Float {
        resetPaint(paint)
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = style.footerTextSize
        paint.isFakeBoldText = false
        paint.color = TEXT_GREY
        val baseline = y + style.footerTopGap + style.footerTextSize
        canvas.drawText("Generated by PipeList Pro", style.width / 2f, baseline, paint)
        return baseline + style.bottomPadding
    }

    fun groupedItems(project: Project): List<Pair<String, List<CartItem>>> {
        val groups = project.items.groupBy { normalizedCategory(it.category) }
        val result = mutableListOf<Pair<String, List<CartItem>>>()
        val seen = mutableSetOf<String>()

        categoryOrder.forEach { cat ->
            groups[cat]?.let { items ->
                result.add(cat to items)
                seen.add(cat)
            }
        }

        groups.keys.forEach { cat ->
            if (cat !in seen) {
                result.add(cat to groups[cat]!!)
            }
        }

        return result
    }

    fun measureItemRowHeight(paint: Paint, style: ExportStyle, item: CartItem, index: Int): Float {
        val lines = wrapText(paint, itemDisplayName(item), style.itemTextWidth, style.itemTextSize).take(2)
        return maxOf(style.rowMinHeight, (lines.size * style.rowLineHeight) + (style.rowVerticalPadding * 2f))
    }

    fun headerHeight(style: ExportStyle): Float = style.appNameTextSize + style.headerLineGap + style.dateTextSize + style.headerBottomGap

    fun safeFileName(name: String): String = name.ifBlank { "Material_List" }.replace(Regex("[^A-Za-z0-9_-]+"), "_").take(48)

    private fun normalizedCategory(category: String): String {
        val trimmed = category.trim()
        val upper = trimmed.uppercase(Locale.getDefault())
        return categoryLookup[upper] ?: trimmed
    }

    private fun itemDisplayName(item: CartItem): String {
        val size = item.size.trim().takeIf { it.isNotBlank() && !it.equals("Standard", ignoreCase = true) }?.let { "$it " }.orEmpty()
        return "$size${item.name.trim().ifBlank { "Material" }}"
    }

    private fun wrapText(paint: Paint, text: String, maxWidth: Float, textSize: Float): List<String> {
        resetPaint(paint)
        paint.textSize = textSize
        val words = text.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (words.isEmpty()) return listOf("")
        val lines = mutableListOf<String>()
        var current = ""
        words.forEach { word ->
            val candidate = if (current.isBlank()) word else "$current $word"
            if (paint.measureText(candidate) <= maxWidth) {
                current = candidate
            } else {
                if (current.isNotBlank()) lines.add(current)
                current = word
            }
        }
        if (current.isNotBlank()) lines.add(current)
        return lines.ifEmpty { listOf("") }
    }

    private fun exportDate(project: Project): String = project.date.ifBlank {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    private fun resetPaint(paint: Paint) {
        paint.reset()
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 1f
    }

    private val PRIMARY_BLUE = 0xFF0057D9.toInt()
    private val CATEGORY_BG = 0xFFEEF5FF.toInt()
    private val OUTSIDE_BG = 0xFFF3F5F8.toInt()
    private val TEXT_DARK = 0xFF111827.toInt()
    private val TEXT_GREY = 0xFF6B7280.toInt()
    private val DIVIDER = 0xFFE8EDF3.toInt()

    fun imageOutsideBackground(): Int = OUTSIDE_BG
}

internal data class ExportStyle(
    val width: Float,
    val horizontalMargin: Float,
    val topPadding: Float,
    val bottomPadding: Float,
    val appNameTextSize: Float,
    val dateTextSize: Float,
    val categoryTextSize: Float,
    val itemTextSize: Float,
    val quantityTextSize: Float,
    val footerTextSize: Float,
    val headerLineGap: Float,
    val headerBottomGap: Float,
    val categoryTopGap: Float,
    val categoryHeight: Float,
    val categoryBottomGap: Float,
    val rowVerticalPadding: Float,
    val rowLineHeight: Float,
    val rowMinHeight: Float,
    val footerTopGap: Float,
    val footerHeight: Float,
    val rowNumberWidth: Float,
    val quantityWidth: Float,
    val categoryCornerRadius: Float
) {
    val left: Float get() = horizontalMargin
    val right: Float get() = width - horizontalMargin
    val rowInnerPadding: Float get() = if (width > 700f) 14f else 7f
    val categoryTextLeftPadding: Float get() = if (width > 700f) 24f else 10f
    val itemTextLeft: Float get() = left + rowInnerPadding + rowNumberWidth
    val itemTextWidth: Float get() = right - rowInnerPadding - quantityWidth - itemTextLeft
}
