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
import com.synfusion.pipelistpro.R
import com.synfusion.pipelistpro.adapter.MaterialAdapter
import com.synfusion.pipelistpro.databinding.FragmentMaterialSearchBinding
import com.synfusion.pipelistpro.viewmodel.ProjectViewModel

class MaterialSearchFragment : Fragment() {
    private var _binding: FragmentMaterialSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProjectViewModel by activityViewModels()
    private lateinit var adapter: MaterialAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMaterialSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        adapter = MaterialAdapter(emptyList()) { material ->
            val bundle = Bundle().apply {
                putParcelable("material", material)
            }
            findNavController().navigate(R.id.action_materialSearch_to_addMaterial, bundle)
        }

        binding.rvMaterials.layoutManager = LinearLayoutManager(context)
        binding.rvMaterials.adapter = adapter

        viewModel.searchResults.observe(viewLifecycleOwner) {
            adapter.updateList(it)
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchMaterials(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
