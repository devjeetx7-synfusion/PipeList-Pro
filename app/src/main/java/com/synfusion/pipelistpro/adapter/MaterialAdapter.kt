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

    // Store quantities outside of ViewHolder to prevent state loss on recycling
    private val quantityMap = mutableMapOf<String, Int>()

    class ViewHolder(val binding: ItemMaterialBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMaterialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = materials[position]

        // Ensure map has entry for item
        if (!quantityMap.containsKey(item.id)) {
            quantityMap[item.id] = 1
        }

        holder.binding.apply {
            tvMaterialName.text = item.name
            tvCategory.text = item.category

            val sizeAdapter = ArrayAdapter(root.context, android.R.layout.simple_spinner_item, item.sizes)
            sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSize.adapter = sizeAdapter

            tvQuantity.text = quantityMap[item.id].toString()

            btnIncreaseQty.setOnClickListener {
                var currentQty = quantityMap[item.id] ?: 1
                currentQty++
                quantityMap[item.id] = currentQty
                tvQuantity.text = currentQty.toString()
            }

            btnDecreaseQty.setOnClickListener {
                var currentQty = quantityMap[item.id] ?: 1
                if (currentQty > 1) {
                    currentQty--
                    quantityMap[item.id] = currentQty
                    tvQuantity.text = currentQty.toString()
                }
            }

            btnAdd.setOnClickListener {
                val selectedSize = if (item.sizes.isNotEmpty()) spinnerSize.selectedItem.toString() else "Standard"
                onAddClick(item, selectedSize, quantityMap[item.id] ?: 1)

                // Reset quantity after adding
                quantityMap[item.id] = 1
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
