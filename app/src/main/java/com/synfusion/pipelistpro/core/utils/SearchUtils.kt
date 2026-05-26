package com.synfusion.pipelistpro.core.utils

import com.synfusion.pipelistpro.data.models.MaterialItem

object SearchUtils {
    /**
     * Filters materials based on a query string.
     * Supports matching by:
     * - Item name
     * - Category
     * - Specific size mention (e.g. "1 inch")
     * - Keywords (e.g. "bend", "suleshan")
     */
    fun filterMaterials(materials: List<MaterialItem>, query: String): List<MaterialItem> {
        if (query.isBlank()) return materials

        val normalizedQuery = query.trim().lowercase()
        val parts = normalizedQuery.split(" ", "\"", "inch").filter { it.isNotBlank() }

        return materials.filter { item ->
            val name = item.name.lowercase()
            val category = item.category.lowercase()
            val keywords = item.searchKeywords

            // Check if all query parts match something in the item
            parts.all { part ->
                name.contains(part) ||
                category.contains(part) ||
                keywords.any { it.contains(part) } ||
                item.sizes.any { it.lowercase().contains(part) } ||
                // Special mapping for common Indian variations
                isIndianSynonymMatch(part, keywords)
            }
        }
    }

    private fun isIndianSynonymMatch(part: String, keywords: List<String>): Boolean {
        return when (part) {
            "bend" -> keywords.contains("bend")
            "suleshan", "solution", "bond", "glue" -> keywords.contains("solution") || keywords.contains("suleshan")
            "jali", "trap", "trap cover" -> keywords.contains("jali") || keywords.contains("floor trap")
            "tap", "faucet", "cock" -> keywords.contains("tap") || keywords.contains("bib cock")
            else -> false
        }
    }
}
