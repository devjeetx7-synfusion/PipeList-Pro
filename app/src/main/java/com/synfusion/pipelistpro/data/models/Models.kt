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
    val category: String,
    @SerializedName(value = "name", alternate = ["materialName"]) val name: String,
    val size: String,
    val unit: String,
    val ft: Double? = null,
    val quantity: Int
) : Parcelable

@Parcelize
data class Project(
    val id: String,
    val projectName: String,
    val clientName: String,
    val location: String,
    val date: String,
    val items: MutableList<CartItem> = mutableListOf()
) : Parcelable
