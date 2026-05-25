package com.synfusion.pipelistpro.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.synfusion.pipelistpro.R
import com.synfusion.pipelistpro.databinding.FragmentNewProjectBinding
import com.synfusion.pipelistpro.viewmodel.ProjectViewModel
import java.text.SimpleDateFormat
import java.util.*

class NewProjectFragment : Fragment() {
    private var _binding: FragmentNewProjectBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProjectViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.etDate.setText(sdf.format(Date()))

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, day ->
                calendar.set(year, month, day)
                binding.etDate.setText(sdf.format(calendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.btnStartAdding.setOnClickListener {
            val name = binding.etProjectName.text.toString()
            val client = binding.etClientName.text.toString()
            val location = binding.etLocation.text.toString()
            val date = binding.etDate.text.toString()

            if (name.isBlank()) {
                Toast.makeText(context, "Please enter project name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.startNewProject(name, client, location, date)
            findNavController().navigate(R.id.action_newProject_to_materialSearch)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
