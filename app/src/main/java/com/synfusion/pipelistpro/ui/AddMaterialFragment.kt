package com.synfusion.pipelistpro.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.synfusion.pipelistpro.R
import com.synfusion.pipelistpro.adapter.MaterialAdapter
import com.synfusion.pipelistpro.data.MaterialCatalog
import com.synfusion.pipelistpro.databinding.FragmentAddMaterialBinding
import com.synfusion.pipelistpro.model.ProjectItem
import com.synfusion.pipelistpro.viewmodel.ProjectViewModel

class AddMaterialFragment : Fragment() {
    private var _binding: FragmentAddMaterialBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProjectViewModel by activityViewModels()
    private lateinit var adapter: MaterialAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddMaterialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ensure we have a project before proceeding. If not, start one automatically.
        if (viewModel.currentProject.value == null) {
             viewModel.startNewProject()
        }

        setupRecyclerView()
        setupListeners()
        setupObservers()

        // Load initial data
        adapter.updateList(MaterialCatalog.materials)
    }

    private fun setupRecyclerView() {
        adapter = MaterialAdapter(emptyList()) { material, size, qty ->
            val projectItem = ProjectItem(
                materialName = material.name,
                category = material.category,
                size = size,
                quantity = qty,
                unit = material.unit
            )
            viewModel.addItemToCurrentProject(projectItem)

            Snackbar.make(binding.root, "Added to List", Snackbar.LENGTH_SHORT).show()
        }
        binding.rvMaterials.layoutManager = LinearLayoutManager(context)
        binding.rvMaterials.adapter = adapter
    }

    private fun setupListeners() {
        binding.layoutSummary.setOnClickListener {
            findNavController().navigate(R.id.action_addMaterial_to_projectList)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack(R.id.homeFragment, false)
            }
        })

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterMaterials(s.toString(), getSelectedCategory())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.chipGroupCategories.setOnCheckedStateChangeListener { _, _ ->
            val query = binding.etSearch.text.toString()
            filterMaterials(query, getSelectedCategory())
        }
    }

    private fun setupObservers() {
        viewModel.currentProject.observe(viewLifecycleOwner) { project ->
            project?.let {
                binding.tvTotalItems.text = "Total Items: ${it.items.size}"
                val totalQty = it.items.sumOf { item -> item.quantity }
                binding.tvTotalQty.text = "Total Qty: $totalQty"
            }
        }
    }

    private fun getSelectedCategory(): String {
        return when (binding.chipGroupCategories.checkedChipId) {
            R.id.chipUPVC -> "UPVC"
            R.id.chipCPVC -> "CPVC"
            R.id.chipPVC -> "PVC"
            R.id.chipSWR -> "SWR"
            R.id.chipGI -> "GI"
            R.id.chipHDPE -> "HDPE"
            else -> "All"
        }
    }

    private fun filterMaterials(query: String, category: String) {
        val filtered = MaterialCatalog.materials.filter {
            val matchesCategory = category == "All" || it.category == category
            val matchesQuery = query.isBlank() || it.name.contains(query, ignoreCase = true) || it.searchKeywords.any { kw -> kw.contains(query, ignoreCase = true) }
            matchesCategory && matchesQuery
        }
        adapter.updateList(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
