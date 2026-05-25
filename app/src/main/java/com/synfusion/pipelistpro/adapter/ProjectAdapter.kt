package com.synfusion.pipelistpro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.synfusion.pipelistpro.databinding.ItemProjectBinding
import com.synfusion.pipelistpro.model.Project

class ProjectAdapter(
    private var projects: List<Project>,
    private val onProjectClick: (Project) -> Unit,
    private val onDuplicateClick: (Project) -> Unit,
    private val onDeleteClick: (Project) -> Unit
) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemProjectBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projects[position]
        holder.binding.apply {
            tvProjectName.text = project.projectName
            tvClientInfo.text = "${project.clientName} | ${project.location}"
            tvDate.text = project.date
            tvItemCount.text = "${project.items.size} Items"

            root.setOnClickListener { onProjectClick(project) }
            btnDuplicate.setOnClickListener { onDuplicateClick(project) }
            btnDeleteProject.setOnClickListener { onDeleteClick(project) }
        }
    }

    override fun getItemCount() = projects.size

    fun updateList(newList: List<Project>) {
        projects = newList
        notifyDataSetChanged()
    }
}
