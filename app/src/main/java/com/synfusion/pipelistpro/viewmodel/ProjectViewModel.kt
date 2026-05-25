package com.synfusion.pipelistpro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.synfusion.pipelistpro.data.MaterialCatalog
import com.synfusion.pipelistpro.model.MaterialItem
import com.synfusion.pipelistpro.model.Project
import com.synfusion.pipelistpro.model.ProjectItem
import com.synfusion.pipelistpro.storage.ProjectStorage
import com.synfusion.pipelistpro.utils.SearchUtils
import java.text.SimpleDateFormat
import java.util.*

class ProjectViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = ProjectStorage(application)

    private val _savedProjects = MutableLiveData<List<Project>>()
    val savedProjects: LiveData<List<Project>> = _savedProjects

    private val _currentProject = MutableLiveData<Project?>()
    val currentProject: LiveData<Project?> = _currentProject

    private val _searchResults = MutableLiveData<List<MaterialItem>>()
    val searchResults: LiveData<List<MaterialItem>> = _searchResults

    init {
        loadSavedProjects()
        _searchResults.value = MaterialCatalog.materials
    }

    fun loadSavedProjects() {
        _savedProjects.value = storage.getProjects()
    }

    fun startNewProject(name: String, client: String, location: String, date: String) {
        val newProject = Project(
            id = UUID.randomUUID().toString(),
            projectName = name,
            clientName = client,
            location = location,
            date = date
        )
        _currentProject.value = newProject
    }

    fun loadProject(project: Project) {
        _currentProject.value = project
    }

    fun addItemToCurrentProject(item: ProjectItem) {
        _currentProject.value?.let { project ->
            project.items.add(item)
            _currentProject.value = project // Trigger update
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

    fun applyTemplate(templateItems: List<ProjectItem>) {
        _currentProject.value?.let { project ->
            project.items.addAll(templateItems)
            _currentProject.value = project
        }
    }
}
