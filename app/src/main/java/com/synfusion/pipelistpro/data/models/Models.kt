package com.synfusion.pipelistpro.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
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
data class CartItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val materialId: String = "",
    val category: String = "Tools/Other",
    @SerializedName(value = "name", alternate = ["materialName"]) val name: String = "Material",
    val size: String = "Standard",
    val unit: String = "pcs",
    val ft: Double? = null,
    val quantity: Int = 1
) : Parcelable

@Parcelize
data class Project(
    val id: String = java.util.UUID.randomUUID().toString(),
    val projectName: String = "Material List",
    val clientName: String = "",
    val location: String = "",
    val date: String = "",
    val notes: String = "",
    val items: List<CartItem> = emptyList()
) : Parcelable

enum class ThemeMode { SYSTEM, LIGHT, DARK }
