package com.synfusion.pipelistpro.core.utils

import com.synfusion.pipelistpro.data.models.MaterialItem

object SearchUtils {
    /**
     * Filters materials based on a query string.
     * Supports matching by:
     * - Item name
     * - Category
     * - Specific size mention (e.g. "1 inch")
     * - Keywords (alias, group, system)
     */
    fun filterMaterials(materials: List<MaterialItem>, query: String): List<MaterialItem> {
        if (query.isBlank()) return materials

        val normalizedQuery = query.trim().lowercase()
        val parts = normalizedQuery.split(" ", "\"", "inch").filter { it.isNotBlank() }

        return materials.filter { item ->
            val name = item.name.lowercase()
            val alias = item.alias.lowercase()
            val category = item.categoryName.lowercase()
            val group = item.group.lowercase()
            val system = item.system.lowercase()

            // Check if all query parts match something in the item
            parts.all { part ->
                name.contains(part) ||
                alias.contains(part) ||
                category.contains(part) ||
                group.contains(part) ||
                system.contains(part) ||
                item.sizes.any { it.lowercase().contains(part) }
            }
        }
    }
}
