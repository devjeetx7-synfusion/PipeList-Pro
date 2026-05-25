package com.synfusion.pipelistpro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.synfusion.pipelistpro.databinding.ItemMaterialBinding
import com.synfusion.pipelistpro.model.MaterialItem

class MaterialAdapter(
    private var materials: List<MaterialItem>,
    private val onItemClick: (MaterialItem) -> Unit
) : RecyclerView.Adapter<MaterialAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMaterialBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMaterialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = materials[position]
        holder.binding.apply {
            tvMaterialName.text = item.name
            tvCategory.text = item.category
            tvSizes.text = "Sizes: ${item.sizes.joinToString(", ")}"
            root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun getItemCount() = materials.size

    fun updateList(newList: List<MaterialItem>) {
        materials = newList
        notifyDataSetChanged()
    }
}
