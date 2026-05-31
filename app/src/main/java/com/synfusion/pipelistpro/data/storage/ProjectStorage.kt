package com.synfusion.pipelistpro.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.synfusion.pipelistpro.data.models.Project

class ProjectStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("PipeListProPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveProjects(projects: List<Project>) {
        prefs.edit().putString(KEY_SAVED_PROJECTS, gson.toJson(projects.map { it.sanitized() })).apply()
    }

    fun getProjects(): List<Project> {
        val json = prefs.getString(KEY_SAVED_PROJECTS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Project>>() {}.type
            gson.fromJson<List<Project>?>(json, type).orEmpty().mapNotNull { it?.sanitized() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveCurrentProject(project: Project?) {
        prefs.edit().apply {
            if (project == null) remove(KEY_CURRENT_PROJECT) else putString(KEY_CURRENT_PROJECT, gson.toJson(project.sanitized()))
        }.apply()
    }

    fun getCurrentProject(): Project? {
        val json = prefs.getString(KEY_CURRENT_PROJECT, null) ?: return null
        return try {
            gson.fromJson(json, Project::class.java)?.sanitized()
        } catch (_: JsonSyntaxException) {
            null
        } catch (_: Exception) {
            null
        }
    }

    fun addProject(project: Project) {
        val projects = getProjects().toMutableList()
        projects.removeAll { it.id == project.id }
        projects.add(0, project.sanitized())
        saveProjects(projects)
    }

    fun updateProject(updatedProject: Project) {
        val projects = getProjects().toMutableList()
        val index = projects.indexOfFirst { it.id == updatedProject.id }
        if (index != -1) projects[index] = updatedProject.sanitized() else projects.add(0, updatedProject.sanitized())
        saveProjects(projects)
    }

    fun deleteProject(projectId: String) {
        saveProjects(getProjects().filterNot { it.id == projectId })
    }

    fun clearProjects() = prefs.edit().remove(KEY_SAVED_PROJECTS).apply()

    fun clearAllLists() = prefs.edit().remove(KEY_SAVED_PROJECTS).remove(KEY_CURRENT_PROJECT).apply()

    private fun Project.sanitized(): Project {
        val safeItems = runCatching { items }.getOrNull().orEmpty().mapNotNull { item ->
            val safeName = runCatching { item.name }.getOrNull().orEmpty().trim()
            if (safeName.isBlank()) return@mapNotNull null
            val safeId = runCatching { item.id }.getOrNull().orEmpty().ifBlank { java.util.UUID.randomUUID().toString() }
            val safeMaterialId = runCatching { item.materialId }.getOrNull().orEmpty().ifBlank { safeName.lowercase().replace(" ", "_") }
            item.copy(
                id = safeId,
                materialId = safeMaterialId,
                name = safeName,
                category = runCatching { item.category }.getOrNull().orEmpty().ifBlank { "Tools/Other" },
                size = runCatching { item.size }.getOrNull().orEmpty().ifBlank { "Standard" },
                unit = runCatching { item.unit }.getOrNull().orEmpty().ifBlank { "pcs" },
                quantity = runCatching { item.quantity }.getOrDefault(1).coerceAtLeast(1)
            )
        }
        val safeDate = runCatching { date }.getOrNull().orEmpty()
        return copy(
            id = runCatching { id }.getOrNull().orEmpty().ifBlank { java.util.UUID.randomUUID().toString() },
            projectName = runCatching { projectName }.getOrNull().orEmpty().ifBlank { "Material List" },
            clientName = runCatching { clientName }.getOrNull().orEmpty(),
            location = runCatching { location }.getOrNull().orEmpty(),
            date = safeDate,
            notes = runCatching { notes }.getOrNull().orEmpty(),
            items = safeItems
        )
    }

    companion object {
        private const val KEY_SAVED_PROJECTS = "saved_projects"
        private const val KEY_CURRENT_PROJECT = "current_project"
    }
}
