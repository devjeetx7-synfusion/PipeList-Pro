package com.synfusion.pipelistpro.features.cart

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.synfusion.pipelistpro.data.repository.MaterialCatalog
import com.synfusion.pipelistpro.data.models.MaterialItem
import com.synfusion.pipelistpro.data.models.Project
import com.synfusion.pipelistpro.data.models.CartItem
import com.synfusion.pipelistpro.data.storage.ProjectStorage
import com.synfusion.pipelistpro.core.utils.SearchUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

class ProjectViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = ProjectStorage(application)
    private val prefs = application.getSharedPreferences("pipelist_prefs", Context.MODE_PRIVATE)

    private val _savedProjects = MutableStateFlow<List<Project>>(emptyList())
    val savedProjects: StateFlow<List<Project>> = _savedProjects

    private val _currentProject = MutableStateFlow<Project?>(null)
    val currentProject: StateFlow<Project?> = _currentProject

    private val _searchResults = MutableStateFlow<List<MaterialItem>>(emptyList())
    val searchResults: StateFlow<List<MaterialItem>> = _searchResults

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    val materialStates = mutableStateMapOf<String, MaterialState>()

    data class MaterialState(val size: String, val quantity: Int, val ft: Double? = null)

    init {
        loadSavedProjects()
        _searchResults.value = MaterialCatalog.materials
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        prefs.edit().putBoolean("dark_mode", enabled).apply()
    }

    fun loadSavedProjects() {
        _savedProjects.value = storage.getProjects()
    }

    fun startNewProject() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateStr = sdf.format(Date())
        val name = "Material List - $dateStr"
        val newProject = Project(
            id = UUID.randomUUID().toString(),
            projectName = name,
            clientName = "",
            location = "",
            date = dateStr
        )
        _currentProject.value = newProject
    }

    fun updateCartItemQuantity(cartItemId: String, newQuantity: Int) {
        if (newQuantity < 1) return

        _currentProject.value?.let { project ->
            val index = project.items.indexOfFirst { it.id == cartItemId }
            if (index != -1) {
                val updatedItems = project.items.toMutableList()
                updatedItems[index] = updatedItems[index].copy(quantity = newQuantity)
                _currentProject.value = project.copy(items = updatedItems)
            }
        }
    }

    fun loadProject(project: Project) {
        _currentProject.value = project
    }

    fun addItemToCurrentProject(item: CartItem) {
        _currentProject.value?.let { project ->
            // Merges item if it matches materialId + size + unit + ft
            val existingIndex = project.items.indexOfFirst {
                it.materialId == item.materialId &&
                it.name == item.name &&
                it.size == item.size &&
                it.unit == item.unit &&
                it.ft == item.ft
            }

            val updatedItems = project.items.toMutableList()
            if (existingIndex != -1) {
                val existingItem = updatedItems[existingIndex]
                updatedItems[existingIndex] = existingItem.copy(
                    quantity = existingItem.quantity + item.quantity
                )
            } else {
                updatedItems.add(item)
            }
            val updatedProject = project.copy(items = updatedItems)
            _currentProject.value = updatedProject
        }
    }

    fun removeItemByItem(targetItem: CartItem) {
        _currentProject.value?.let { project ->
            val index = project.items.indexOfFirst { it.id == targetItem.id }
            if (index != -1) {
                project.items.removeAt(index)
                val updatedProject = project.copy(items = project.items.toMutableList())
                _currentProject.value = updatedProject
            }
        }
    }

    fun removeItemFromCurrentProject(position: Int) {
        _currentProject.value?.let { project ->
            if (position in project.items.indices) {
                project.items.removeAt(position)
                _currentProject.value = project // Trigger update
            }
        }
    }

    fun saveCurrentProject() {
        _currentProject.value?.let { project ->
            val projects = storage.getProjects().toMutableList()
            val existingIndex = projects.indexOfFirst { it.id == project.id }
            if (existingIndex != -1) {
                projects[existingIndex] = project
            } else {
                projects.add(0, project)
            }
            storage.saveProjects(projects)
            loadSavedProjects()
        }
    }

    fun duplicateProject(project: Project) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val newProject = project.copy(
            id = UUID.randomUUID().toString(),
            projectName = "${project.projectName} (Copy)",
            date = sdf.format(Date()),
            items = project.items.toMutableList()
        )
        val projects = storage.getProjects().toMutableList()
        projects.add(0, newProject)
        storage.saveProjects(projects)
        loadSavedProjects()
    }

    fun deleteProject(projectId: String) {
        storage.deleteProject(projectId)
        loadSavedProjects()
    }

    fun searchMaterials(query: String) {
        _searchResults.value = SearchUtils.filterMaterials(MaterialCatalog.materials, query)
    }

    fun applyTemplate(templateItems: List<CartItem>) {
        _currentProject.value?.let { project ->
            val updatedItems = project.items.toMutableList()
            updatedItems.addAll(templateItems)
            val updatedProject = project.copy(items = updatedItems)
            _currentProject.value = updatedProject
        }
    }

    fun getMaterialState(materialId: String, defaultSize: String): MaterialState {
        return materialStates.getOrPut(materialId) { MaterialState(defaultSize, 1) }
    }

    fun removeCartItem(cartItemId: String) {
        _currentProject.value?.let { project ->
            val updatedItems = project.items.filter { it.id != cartItemId }.toMutableList()
            _currentProject.value = project.copy(items = updatedItems)
        }
    }

    fun updateMaterialSize(materialId: String, newSize: String) {
        val currentState = materialStates[materialId] ?: MaterialState(newSize, 1)
        materialStates[materialId] = currentState.copy(size = newSize)
    }

    fun updateMaterialQuantity(materialId: String, newQuantity: Int) {
        val currentState = materialStates[materialId] ?: MaterialState("", newQuantity)
        materialStates[materialId] = currentState.copy(quantity = newQuantity)
    }

    fun updateMaterialFt(materialId: String, ft: Double?) {
        val currentState = materialStates[materialId] ?: MaterialState("", 1, ft)
        materialStates[materialId] = currentState.copy(ft = ft)
    }
}
