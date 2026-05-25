package com.synfusion.pipelistpro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.synfusion.pipelistpro.databinding.ItemProjectItemBinding
import com.synfusion.pipelistpro.model.ProjectItem

class ProjectItemAdapter(
    private var items: List<ProjectItem>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<ProjectItemAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemProjectItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProjectItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            tvMaterialName.text = item.materialName
            tvDetails.text = "${item.category} | ${item.size}"
            tvQuantity.text = "${item.quantity} ${item.unit}"

            if (item.notes.isNotEmpty()) {
                tvNotes.text = item.notes
                tvNotes.visibility = View.VISIBLE
            } else {
                tvNotes.visibility = View.GONE
            }

            btnDelete.setOnClickListener { onDeleteClick(holder.adapterPosition) }
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newList: List<ProjectItem>) {
        items = newList
        notifyDataSetChanged()
    }
}
