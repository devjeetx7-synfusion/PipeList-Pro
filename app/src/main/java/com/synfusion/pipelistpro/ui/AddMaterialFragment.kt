package com.synfusion.pipelistpro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.synfusion.pipelistpro.R
import com.synfusion.pipelistpro.databinding.FragmentAddMaterialBinding
import com.synfusion.pipelistpro.model.MaterialItem
import com.synfusion.pipelistpro.model.ProjectItem
import com.synfusion.pipelistpro.viewmodel.ProjectViewModel

class AddMaterialFragment : Fragment() {
    private var _binding: FragmentAddMaterialBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProjectViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddMaterialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val material = arguments?.getParcelable<MaterialItem>("material") ?: return

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.tvMaterialName.text = material.name
        binding.tvCategory.text = material.category
        binding.etUnit.setText(material.unit)

        val sizeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, material.sizes)
        binding.spinnerSize.setAdapter(sizeAdapter)
        if (material.sizes.isNotEmpty()) {
            binding.spinnerSize.setText(material.sizes[0], false)
        }

        binding.btnAddToList.setOnClickListener {
            val size = binding.spinnerSize.text.toString()
            val qtyStr = binding.etQuantity.text.toString()
            val notes = binding.etNotes.text.toString()

            if (qtyStr.isBlank() || qtyStr.toInt() <= 0) {
                Toast.makeText(context, "Enter valid quantity", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val projectItem = ProjectItem(
                materialName = material.name,
                category = material.category,
                size = size,
                quantity = qtyStr.toInt(),
                unit = material.unit,
                notes = notes
            )

            viewModel.addItemToCurrentProject(projectItem)
            Toast.makeText(context, "Added to list", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addMaterial_to_projectList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
