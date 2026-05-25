package com.synfusion.pipelistpro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.synfusion.pipelistpro.R
import com.synfusion.pipelistpro.adapter.ProjectItemAdapter
import com.synfusion.pipelistpro.databinding.FragmentProjectListBinding
import com.synfusion.pipelistpro.pdf.PdfGenerator
import com.synfusion.pipelistpro.utils.ShareUtils
import com.synfusion.pipelistpro.viewmodel.ProjectViewModel

class ProjectListFragment : Fragment() {
    private var _binding: FragmentProjectListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProjectViewModel by activityViewModels()
    private lateinit var adapter: ProjectItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProjectListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        adapter = ProjectItemAdapter(
            items = emptyList(),
            onIncreaseQty = { item, newQty ->
                viewModel.updateItemQuantityByItem(item, newQty)
            },
            onDecreaseQty = { item, newQty ->
                viewModel.updateItemQuantityByItem(item, newQty)
            },
            onDeleteClick = { item ->
                viewModel.removeItemByItem(item)
            }
        )

        binding.rvProjectItems.layoutManager = LinearLayoutManager(context)
        binding.rvProjectItems.adapter = adapter

        viewModel.currentProject.observe(viewLifecycleOwner) { project ->
            project?.let {
                binding.tvProjectDetails.text = it.projectName

                val totalQty = it.items.sumOf { item -> item.quantity }
                binding.tvTotalSummary.text = "Total Items: ${it.items.size} | Total Qty: $totalQty"

                // Sort items by category to group them implicitly
                val sortedItems = it.items.sortedBy { item -> item.category }
                adapter.updateList(sortedItems)
            }
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_projectList_to_addMaterial)
        }

        binding.btnSave.setOnClickListener {
            viewModel.saveCurrentProject()
            Toast.makeText(context, "Project Saved", Toast.LENGTH_SHORT).show()
        }

        binding.btnShare.setOnClickListener {
            viewModel.currentProject.value?.let { project ->
                ShareUtils.shareProjectAsText(requireContext(), project)
            }
        }

        binding.btnExportPdf.setOnClickListener {
            viewModel.currentProject.value?.let { project ->
                val file = PdfGenerator.generateProjectPdf(requireContext(), project)
                if (file != null) {
                    ShareUtils.sharePdfFile(requireContext(), file)
                } else {
                    Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
