package com.synfusion.pipelistpro.data.repository

import android.content.Context
import com.google.gson.Gson
import com.synfusion.pipelistpro.data.models.MaterialCategory
import com.synfusion.pipelistpro.data.models.MaterialItem
import com.synfusion.pipelistpro.data.models.PlumbingMaterialDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class MaterialRepository(private val context: Context) {
    private val gson = Gson()
    private var database: PlumbingMaterialDatabase? = null

    suspend fun getDatabase(): PlumbingMaterialDatabase = withContext(Dispatchers.IO) {
        database ?: loadDatabase().also { database = it }
    }

    private fun loadDatabase(): PlumbingMaterialDatabase {
        return try {
            context.assets.open("plumbing_materials.json").use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    gson.fromJson(reader, PlumbingMaterialDatabase::class.java)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            PlumbingMaterialDatabase(1, emptyList())
        }
    }

    suspend fun getCategories(): List<MaterialCategory> = getDatabase().categories

    suspend fun getMaterialsByCategory(categoryId: String): List<MaterialItem> {
        return getDatabase().categories.find { it.id == categoryId }?.items ?: emptyList()
    }

    suspend fun getAllMaterials(): List<MaterialItem> {
        return getDatabase().categories.flatMap { it.items }
    }

    suspend fun searchMaterials(query: String): List<MaterialItem> {
        if (query.isBlank()) return emptyList()
        val all = getAllMaterials()
        val lowerQuery = query.lowercase().trim()
        return all.filter { item ->
            item.name.lowercase().contains(lowerQuery) ||
            item.alias.lowercase().contains(lowerQuery) ||
            item.group.lowercase().contains(lowerQuery) ||
            item.categoryName.lowercase().contains(lowerQuery) ||
            item.system.lowercase().contains(lowerQuery)
        }
    }
}
