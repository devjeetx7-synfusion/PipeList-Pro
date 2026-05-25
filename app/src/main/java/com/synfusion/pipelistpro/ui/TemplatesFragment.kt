package com.synfusion.pipelistpro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.synfusion.pipelistpro.R
import com.synfusion.pipelistpro.data.MaterialCatalog
import com.synfusion.pipelistpro.databinding.FragmentTemplatesBinding
import com.synfusion.pipelistpro.viewmodel.ProjectViewModel

class TemplatesFragment : Fragment() {
    private var _binding: FragmentTemplatesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProjectViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTemplatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.cardBathroom.setOnClickListener {
            applyTemplate("Bathroom", MaterialCatalog.getBathroomTemplate())
        }

        binding.cardKitchen.setOnClickListener {
            applyTemplate("Kitchen", MaterialCatalog.getKitchenTemplate())
        }

        binding.cardWaterTank.setOnClickListener {
            applyTemplate("Water Tank", MaterialCatalog.getWaterTankTemplate())
        }

        binding.cardDrainage.setOnClickListener {
            applyTemplate("Drainage", MaterialCatalog.getDrainageTemplate())
        }
    }

    private fun applyTemplate(name: String, items: List<com.synfusion.pipelistpro.model.ProjectItem>) {
        if (viewModel.currentProject.value == null) {
            viewModel.startNewProject("$name Template", "Template", "N/A", "")
        }
        viewModel.applyTemplate(items)
        Toast.makeText(context, "$name items added", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_templates_to_projectList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
