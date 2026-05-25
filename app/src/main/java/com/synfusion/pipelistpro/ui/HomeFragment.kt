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
            findNavController().navigate(R.id.action_home_to_newProject)
        }

        binding.btnSavedLists.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_savedProjects)
        }

        binding.cardTemplates.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_templates)
        }

        binding.cardCatalog.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_materialSearch)
        }

        binding.cardSettings.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_settings)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
