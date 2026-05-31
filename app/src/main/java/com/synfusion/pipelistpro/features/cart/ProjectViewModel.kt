package com.synfusion.pipelistpro.features.cart

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import com.synfusion.pipelistpro.core.utils.SearchUtils
import com.synfusion.pipelistpro.data.models.CartItem
import com.synfusion.pipelistpro.data.models.MaterialItem
import com.synfusion.pipelistpro.data.models.Project
import com.synfusion.pipelistpro.data.models.ThemeMode
import com.synfusion.pipelistpro.data.repository.MaterialCatalog
import com.synfusion.pipelistpro.data.storage.ProjectStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.abs

class ProjectViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = ProjectStorage(application)
    private val prefs = application.getSharedPreferences("pipelist_prefs", Context.MODE_PRIVATE)

    private val _savedProjects = MutableStateFlow<List<Project>>(emptyList())
    val savedProjects: StateFlow<List<Project>> = _savedProjects

    private val _currentProject = MutableStateFlow<Project?>(null)
    val currentProject: StateFlow<Project?> = _currentProject

    private val _searchResults = MutableStateFlow(MaterialCatalog.materials)
    val searchResults: StateFlow<List<MaterialItem>> = _searchResults

    private val initialThemeMode = runCatching {
        ThemeMode.valueOf(prefs.getString(KEY_THEME_MODE, null) ?: "")
    }.getOrNull() ?: if (prefs.getBoolean("dark_mode", false)) ThemeMode.DARK else ThemeMode.SYSTEM

    private val _themeMode = MutableStateFlow(initialThemeMode)
    val themeMode: StateFlow<ThemeMode> = _themeMode

    private val _isDarkMode = MutableStateFlow(initialThemeMode == ThemeMode.DARK)
    @Deprecated("Use themeMode instead")
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    val materialStates = mutableStateMapOf<String, MaterialState>()

    data class MaterialState(val size: String, val quantity: Int, val ft: Double? = null)

    init {
        loadSavedProjects()
        _currentProject.value = storage.getCurrentProject()
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit()
            .putString(KEY_THEME_MODE, mode.name)
            .putBoolean("dark_mode", mode == ThemeMode.DARK)
            .apply()
        _isDarkMode.value = mode == ThemeMode.DARK
    }

    fun toggleDarkMode(enabled: Boolean) = setThemeMode(if (enabled) ThemeMode.DARK else ThemeMode.LIGHT)

    fun loadSavedProjects() {
        _savedProjects.value = storage.getProjects()
    }

    fun startNewProject() {
        val dateStr = currentDate()
        setCurrentProject(
            Project(
                id = UUID.randomUUID().toString(),
                projectName = "Material List - $dateStr",
                clientName = "",
                location = "",
                date = dateStr,
                notes = "",
                items = emptyList()
            )
        )
        materialStates.clear()
    }

    fun ensureProjectStarted() {
        if (_currentProject.value == null) startNewProject()
    }

    fun loadProject(project: Project) {
        setCurrentProject(project.copy(items = project.items.map { it.copy(quantity = it.quantity.coerceAtLeast(1)) }))
        materialStates.clear()
    }

    fun updateProjectDetails(projectName: String, notes: String) {
        val project = _currentProject.value ?: return
        val safeName = projectName.trim().ifBlank { "Material List - ${project.date.ifBlank { currentDate() }}" }
        setCurrentProject(project.copy(projectName = safeName, notes = notes.trim()))
    }

    fun addItemToCurrentProject(item: CartItem) {
        ensureProjectStarted()
        val project = _currentProject.value ?: return
        val safeItem = item.copy(quantity = item.quantity.coerceAtLeast(1), ft = item.ft?.takeIf { it > 0.0 })
        val updatedItems = project.items.toMutableList()
        val existingIndex = updatedItems.indexOfFirst { it.isSameLineAs(safeItem) }
        if (existingIndex != -1) {
            val existingItem = updatedItems[existingIndex]
            updatedItems[existingIndex] = existingItem.copy(quantity = existingItem.quantity + safeItem.quantity)
        } else {
            updatedItems.add(safeItem)
        }
        setCurrentProject(project.copy(items = updatedItems))
    }

    fun updateCartItemQuantity(cartItemId: String, newQuantity: Int) {
        val project = _currentProject.value ?: return
        if (newQuantity < 1) return
        val updatedItems = project.items.map { if (it.id == cartItemId) it.copy(quantity = newQuantity) else it }
        setCurrentProject(project.copy(items = updatedItems))
    }

    fun updateCartItem(cartItemId: String, size: String, ft: Double?, unit: String) {
        val project = _currentProject.value ?: return
        val current = project.items.firstOrNull { it.id == cartItemId } ?: return
        val edited = current.copy(size = size.ifBlank { "Standard" }, ft = ft?.takeIf { it > 0.0 }, unit = unit.ifBlank { current.unit })
        val remaining = project.items.filterNot { it.id == cartItemId }.toMutableList()
        val mergeIndex = remaining.indexOfFirst { it.isSameLineAs(edited) }
        if (mergeIndex >= 0) {
            remaining[mergeIndex] = remaining[mergeIndex].copy(quantity = remaining[mergeIndex].quantity + edited.quantity)
        } else {
            remaining.add(edited)
        }
        setCurrentProject(project.copy(items = remaining))
    }


    fun updateSelectedMaterialLine(
        materialId: String,
        name: String,
        category: String,
        size: String,
        unit: String,
        ft: Double?,
        quantity: Int
    ) {
        val project = _currentProject.value ?: return
        val safeQuantity = quantity.coerceAtLeast(1)
        val candidate = CartItem(
            materialId = materialId,
            name = name,
            category = category,
            size = size.ifBlank { "Standard" },
            unit = unit.ifBlank { "pcs" },
            ft = ft?.takeIf { it > 0.0 },
            quantity = safeQuantity
        )
        val updatedItems = project.items.toMutableList()
        val exactIndex = updatedItems.indexOfFirst { it.isSameLineAs(candidate) }
        if (exactIndex >= 0) {
            updatedItems[exactIndex] = updatedItems[exactIndex].copy(quantity = safeQuantity)
            setCurrentProject(project.copy(items = updatedItems))
            return
        }

        val sameMaterialIndexes = updatedItems.withIndex()
            .filter { (_, item) ->
                item.materialId == materialId ||
                    (item.name.equals(name, ignoreCase = true) && item.category.equals(category, ignoreCase = true))
            }
            .map { it.index }

        if (sameMaterialIndexes.size == 1) {
            val targetIndex = sameMaterialIndexes.first()
            val edited = updatedItems[targetIndex].copy(
                size = candidate.size,
                unit = candidate.unit,
                ft = candidate.ft,
                quantity = safeQuantity
            )
            updatedItems.removeAt(targetIndex)
            val mergeIndex = updatedItems.indexOfFirst { it.isSameLineAs(edited) }
            if (mergeIndex >= 0) {
                updatedItems[mergeIndex] = updatedItems[mergeIndex].copy(quantity = edited.quantity)
            } else {
                updatedItems.add(targetIndex.coerceAtMost(updatedItems.size), edited)
            }
            setCurrentProject(project.copy(items = updatedItems))
        }
    }

    fun removeCartItem(cartItemId: String) {
        val project = _currentProject.value ?: return
        setCurrentProject(project.copy(items = project.items.filterNot { it.id == cartItemId }))
    }

    fun restoreCartItem(item: CartItem) = addItemToCurrentProject(item)

    fun clearCurrentProject() {
        setCurrentProject(null)
        materialStates.clear()
    }

    fun saveCurrentProject() {
        val project = _currentProject.value ?: return
        val safeProject = project.copy(
            projectName = project.projectName.ifBlank { "Material List - ${project.date.ifBlank { currentDate() }}" },
            date = project.date.ifBlank { currentDate() },
            items = project.items.map { it.copy(quantity = it.quantity.coerceAtLeast(1)) }
        )
        storage.addProject(safeProject)
        setCurrentProject(safeProject)
        loadSavedProjects()
    }

    fun duplicateProject(project: Project) {
        val newProject = project.copy(
            id = UUID.randomUUID().toString(),
            projectName = "${project.projectName} (Copy)",
            date = currentDate(),
            items = project.items.map { it.copy(id = UUID.randomUUID().toString()) }
        )
        storage.addProject(newProject)
        loadSavedProjects()
    }

    fun deleteProject(projectId: String) {
        storage.deleteProject(projectId)
        if (_currentProject.value?.id == projectId) clearCurrentProject()
        loadSavedProjects()
    }

    fun clearSavedProjects() {
        storage.clearProjects()
        loadSavedProjects()
    }

    fun clearAllLists() {
        storage.clearAllLists()
        _savedProjects.value = emptyList()
        _currentProject.value = null
        materialStates.clear()
    }

    fun searchMaterials(query: String) {
        _searchResults.value = SearchUtils.filterMaterials(MaterialCatalog.materials, query)
    }

    fun applyTemplate(templateItems: List<CartItem>) {
        templateItems.forEach { addItemToCurrentProject(it) }
    }

    fun getMaterialState(materialId: String, defaultSize: String): MaterialState {
        return materialStates.getOrPut(materialId) { MaterialState(defaultSize, 1) }
    }

    fun updateMaterialSize(materialId: String, newSize: String) {
        val currentState = materialStates[materialId] ?: MaterialState(newSize, 1)
        materialStates[materialId] = currentState.copy(size = newSize)
    }

    fun updateMaterialQuantity(materialId: String, newQuantity: Int) {
        val currentState = materialStates[materialId] ?: MaterialState("", 1)
        materialStates[materialId] = currentState.copy(quantity = newQuantity.coerceAtLeast(1))
    }

    fun updateMaterialFt(materialId: String, ft: Double?) {
        val currentState = materialStates[materialId] ?: MaterialState("", 1, ft)
        materialStates[materialId] = currentState.copy(ft = ft?.takeIf { it > 0.0 })
    }

    private fun setCurrentProject(project: Project?) {
        _currentProject.value = project
        storage.saveCurrentProject(project)
    }

    private fun CartItem.isSameLineAs(other: CartItem): Boolean {
        val sameFt = when {
            ft == null && other.ft == null -> true
            ft != null && other.ft != null -> abs(ft - other.ft) < 0.001
            else -> false
        }
        return materialId == other.materialId &&
            name.equals(other.name, ignoreCase = true) &&
            category.equals(other.category, ignoreCase = true) &&
            size.equals(other.size, ignoreCase = true) &&
            unit.equals(other.unit, ignoreCase = true) &&
            sameFt
    }

    private fun currentDate(): String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
    }
}
