package com.synfusion.pipelistpro.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.synfusion.pipelistpro.model.Project

class ProjectStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("PipeListProPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveProjects(projects: List<Project>) {
        val json = gson.toJson(projects)
        prefs.edit().putString("saved_projects", json).apply()
    }

    fun getProjects(): List<Project> {
        val json = prefs.getString("saved_projects", null) ?: return emptyList()
        val type = object : TypeToken<List<Project>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addProject(project: Project) {
        val projects = getProjects().toMutableList()
        projects.add(0, project)
        saveProjects(projects)
    }

    fun updateProject(updatedProject: Project) {
        val projects = getProjects().toMutableList()
        val index = projects.indexOfFirst { it.id == updatedProject.id }
        if (index != -1) {
            projects[index] = updatedProject
            saveProjects(projects)
        }
    }

    fun deleteProject(projectId: String) {
        val projects = getProjects().toMutableList()
        projects.removeAll { it.id == projectId }
        saveProjects(projects)
    }
}
