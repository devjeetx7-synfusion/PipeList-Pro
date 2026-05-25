package com.synfusion.pipelistpro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.synfusion.pipelistpro.R
import com.synfusion.pipelistpro.databinding.FragmentHomeBinding
import com.synfusion.pipelistpro.viewmodel.ProjectViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProjectViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNewList.setOnClickListener {
            viewModel.startNewProject()
            findNavController().navigate(R.id.action_home_to_projectList)
        }

        val adapter = com.synfusion.pipelistpro.adapter.ProjectAdapter(
            projects = emptyList(),
            onProjectClick = { project ->
                viewModel.loadProject(project)
                findNavController().navigate(R.id.action_home_to_projectList)
            },
            onDuplicateClick = { project ->
                viewModel.duplicateProject(project)
            },
            onDeleteClick = { project ->
                viewModel.deleteProject(project.id)
            }
        )
        binding.rvSavedLists.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        binding.rvSavedLists.adapter = adapter

        viewModel.savedProjects.observe(viewLifecycleOwner) { projects ->
            adapter.updateList(projects)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
