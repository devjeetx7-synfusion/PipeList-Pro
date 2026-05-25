package com.synfusion.pipelistpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.synfusion.pipelistpro.data.MaterialCatalog
import com.synfusion.pipelistpro.model.ProjectItem
import com.synfusion.pipelistpro.ui.components.*
import com.synfusion.pipelistpro.ui.theme.PipeListProTheme

@Composable
fun MaterialScreen(viewModel: com.synfusion.pipelistpro.viewmodel.ProjectViewModel, navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.observeAsState(emptyList())
    val categories = listOf("All", "CPVC", "UPVC", "SWR", "Bathroom", "Accessories")
    var selectedCategory by remember { mutableStateOf("All") }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopHeader(
            title = "Materials",
            subtitle = "Find and add materials to your list"
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.searchMaterials(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            placeholder = { Text("Search materials...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            items(categories) { category ->
                MaterialCategoryChip(
                    category = category,
                    isSelected = selectedCategory == category,
                    onClick = { selectedCategory = category }
                )
            }
        }

        val filteredResults = if (selectedCategory == "All") {
            searchResults
        } else {
            searchResults.filter { it.category == selectedCategory }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp)
        ) {
            items(filteredResults) { material ->
                val materialState = viewModel.getMaterialState(material.id, material.sizes.firstOrNull() ?: "")
                val selectedSize = materialState.size
                val quantity = materialState.quantity

                MaterialItemCard(
                    name = material.name,
                    category = material.category,
                    size = selectedSize,
                    quantity = quantity,
                    onQuantityChange = { viewModel.updateMaterialQuantity(material.id, it) },
                    onAddClick = {
                        viewModel.addItemToCurrentProject(
                            ProjectItem(
                                materialName = material.name,
                                category = material.category,
                                size = selectedSize,
                                quantity = quantity,
                                unit = material.unit
                            )
                        )
                    }
                )

                if (material.sizes.size > 1) {
                    ScrollableTabRow(
                        selectedTabIndex = material.sizes.indexOf(selectedSize),
                        edgePadding = 0.dp,
                        containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        divider = {},
                        indicator = {}
                    ) {
                        material.sizes.forEach { size ->
                            Tab(
                                selected = selectedSize == size,
                                onClick = { viewModel.updateMaterialSize(material.id, size) },
                                text = { Text(size, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MaterialScreenPreview() {
    PipeListProTheme {
        MaterialScreen(
            viewModel = com.synfusion.pipelistpro.viewmodel.ProjectViewModel(android.app.Application()),
            navController = rememberNavController()
        )
    }
}
