package com.synfusion.pipelistpro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.synfusion.pipelistpro.databinding.ItemMaterialBinding
import com.synfusion.pipelistpro.model.MaterialItem

class MaterialAdapter(
    private var materials: List<MaterialItem>,
    private val onAddClick: (MaterialItem, String, Int) -> Unit
) : RecyclerView.Adapter<MaterialAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMaterialBinding) : RecyclerView.ViewHolder(binding.root) {
        var currentQuantity = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMaterialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = materials[position]
        holder.binding.apply {
            tvMaterialName.text = item.name
            tvCategory.text = item.category

            val sizeAdapter = ArrayAdapter(root.context, android.R.layout.simple_spinner_item, item.sizes)
            sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSize.adapter = sizeAdapter

            holder.currentQuantity = 1
            tvQuantity.text = holder.currentQuantity.toString()

            btnIncreaseQty.setOnClickListener {
                holder.currentQuantity++
                tvQuantity.text = holder.currentQuantity.toString()
            }

            btnDecreaseQty.setOnClickListener {
                if (holder.currentQuantity > 1) {
                    holder.currentQuantity--
                    tvQuantity.text = holder.currentQuantity.toString()
                }
            }

            btnAdd.setOnClickListener {
                val selectedSize = if (item.sizes.isNotEmpty()) spinnerSize.selectedItem.toString() else "Standard"
                onAddClick(item, selectedSize, holder.currentQuantity)

                // Reset quantity after adding
                holder.currentQuantity = 1
                tvQuantity.text = "1"
            }
        }
    }

    override fun getItemCount() = materials.size

    fun updateList(newList: List<MaterialItem>) {
        materials = newList
        notifyDataSetChanged()
    }
}
