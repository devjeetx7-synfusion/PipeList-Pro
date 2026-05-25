package com.synfusion.pipelistpro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.synfusion.pipelistpro.R
import com.synfusion.pipelistpro.adapter.ProjectAdapter
import com.synfusion.pipelistpro.databinding.FragmentSavedProjectsBinding
import com.synfusion.pipelistpro.viewmodel.ProjectViewModel

class SavedProjectsFragment : Fragment() {
    private var _binding: FragmentSavedProjectsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProjectViewModel by activityViewModels()
    private lateinit var adapter: ProjectAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSavedProjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        adapter = ProjectAdapter(
            projects = emptyList(),
            onProjectClick = { project ->
                viewModel.loadProject(project)
                findNavController().navigate(R.id.action_savedProjects_to_projectList)
            },
            onDeleteClick = { project ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Project")
                    .setMessage("Are you sure you want to delete '${project.projectName}'?")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteProject(project.id)
                        Toast.makeText(context, "Project deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        binding.rvSavedProjects.layoutManager = LinearLayoutManager(context)
        binding.rvSavedProjects.adapter = adapter

        viewModel.savedProjects.observe(viewLifecycleOwner) {
            adapter.updateList(it)
            binding.tvEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
