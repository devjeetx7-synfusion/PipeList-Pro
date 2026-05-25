package com.synfusion.pipelistpro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.synfusion.pipelistpro.data.MaterialCatalog
import com.synfusion.pipelistpro.model.ProjectItem
import com.synfusion.pipelistpro.ui.components.MaterialCategoryChip
import com.synfusion.pipelistpro.ui.components.MaterialItemCard
import com.synfusion.pipelistpro.viewmodel.ProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialScreen(viewModel: ProjectViewModel, navController: NavController) {
    val searchResults by viewModel.searchResults.observeAsState(emptyList())
    val currentProject by viewModel.currentProject.observeAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("UPVC") }

    val categories = listOf("UPVC", "CPVC", "PVC", "SWR", "GI", "HDPE", "Tools/Other")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Materials", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { navController.navigate("project_list") }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                        if ((currentProject?.items?.size ?: 0) > 0) {
                            Surface(
                                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.error
                            ) {
                                Text(
                                    text = currentProject?.items?.size.toString(),
                                    modifier = Modifier.padding(horizontal = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchMaterials(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search materials...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // Categories
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    MaterialCategoryChip(
                        category = category,
                        isSelected = selectedCategory == category,
                        onClick = { selectedCategory = category }
                    )
                }
            }

            // Material List
            val filteredMaterials = if (searchQuery.isEmpty()) {
                searchResults.filter { it.category == selectedCategory }
            } else {
                searchResults
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(filteredMaterials) { material ->
                    val materialState = viewModel.getMaterialState(material.id, material.sizes.first())

                    MaterialItemCard(
                        name = material.name,
                        category = material.category,
                        size = materialState.size,
                        unit = material.unit,
                        quantity = materialState.quantity,
                        onQuantityChange = { viewModel.updateMaterialQuantity(material.id, it) },
                        onAddClick = {
                            viewModel.addItemToCurrentProject(
                                ProjectItem(
                                    materialName = material.name,
                                    category = material.category,
                                    size = materialState.size,
                                    quantity = materialState.quantity,
                                    unit = material.unit
                                )
                            )
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
