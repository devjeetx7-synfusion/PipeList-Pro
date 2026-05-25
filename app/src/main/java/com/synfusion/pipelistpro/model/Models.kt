package com.synfusion.pipelistpro.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MaterialItem(
    val id: String,
    val name: String,
    val category: String,
    val sizes: List<String>,
    val unit: String,
    val searchKeywords: List<String>
) : Parcelable

@Parcelize
data class ProjectItem(
    val materialName: String,
    val category: String,
    val size: String,
    val quantity: Int,
    val unit: String,
    val notes: String = ""
) : Parcelable

@Parcelize
data class Project(
    val id: String,
    val projectName: String,
    val clientName: String,
    val location: String,
    val date: String,
    val items: MutableList<ProjectItem> = mutableListOf()
) : Parcelable
