package com.synfusion.pipelistpro.features.materials

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.synfusion.pipelistpro.data.models.CartItem
import com.synfusion.pipelistpro.ui.components.MaterialCategoryChip
import com.synfusion.pipelistpro.ui.components.MaterialItemCard
import com.synfusion.pipelistpro.features.cart.ProjectViewModel
import com.synfusion.pipelistpro.features.cart.CartItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialScreen(viewModel: ProjectViewModel, navController: NavController) {
    val searchResults by viewModel.searchResults.collectAsState(emptyList())
    val currentProject by viewModel.currentProject.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("UPVC") }
    var showCartSheet by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { viewModel.ensureProjectStarted() }

    val categories = listOf("UPVC", "CPVC", "PVC", "SWR", "GI", "HDPE", "Tools/Other")

    if (showCartSheet) {
        SelectedItemsBottomSheet(
            viewModel = viewModel,
            navController = navController,
            onDismiss = { showCartSheet = false },
            onContinue = { showCartSheet = false }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
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
                IconButton(onClick = { showCartSheet = true }) {
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
                                    color = MaterialTheme.colorScheme.onError
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
            TextField(
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
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
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
            val filteredMaterials = if (searchQuery.isBlank()) {
                searchResults.filter { it.category == selectedCategory }
            } else {
                searchResults
            }

            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                items(filteredMaterials, key = { it.id }) { material ->
                    val materialState = viewModel.getMaterialState(material.id, material.sizes.first())
                    val itemsInCart = currentProject?.items?.filter {
                        it.name == material.name && it.category == material.category
                    } ?: emptyList()

                    val totalQtyInCart = itemsInCart.sumOf { it.quantity }

                    MaterialItemCard(
                        name = material.name,
                        category = material.category,
                        size = materialState.size,
                        sizes = material.sizes,
                        unit = material.unit,
                        ft = materialState.ft,
                        quantity = materialState.quantity,
                        inCartCount = totalQtyInCart,
                        onSizeChange = { viewModel.updateMaterialSize(material.id, it) },
                        onFtChange = { viewModel.updateMaterialFt(material.id, it) },
                        onQuantityChange = { viewModel.updateMaterialQuantity(material.id, it) },
                        onAddClick = {
                            val finalFt = if (material.unit == "ft") materialState.ft ?: 1.0 else null
                            viewModel.addItemToCurrentProject(
                                CartItem(
                                    materialId = material.id,
                                    name = material.name,
                                    category = material.category,
                                    size = materialState.size,
                                    quantity = materialState.quantity,
                                    unit = material.unit,
                                    ft = finalFt
                                )
                            )
                        }
                    )
                }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }

                // Mini Cart Bar
                val totalItems = currentProject?.items?.size ?: 0
                if (totalItems > 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 5.dp,
                        onClick = { showCartSheet = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                val uniqueItemCount = currentProject?.items?.size ?: 0
                                Text(
                                    text = "$uniqueItemCount ${if (uniqueItemCount == 1) "item" else "items"} selected",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "View List",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedItemsBottomSheet(
    viewModel: ProjectViewModel,
    navController: NavController,
    onDismiss: () -> Unit,
    onContinue: () -> Unit
) {
    val currentProject by viewModel.currentProject.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Selected Materials",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            if (currentProject == null || currentProject!!.items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No materials added yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                val groupedItems = currentProject!!.items.groupBy { it.category }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(horizontal = 24.dp)
                ) {
                    groupedItems.forEach { (category, items) ->
                        item {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                        items(items) { item ->
                            CartItemCard(
                                item = item,
                                onQuantityChange = { newQty -> viewModel.updateCartItemQuantity(item.id, newQty) },
                                onRemove = { viewModel.removeCartItem(item.id) }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onContinue,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Keep Adding", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        onContinue()
                        viewModel.saveCurrentProject()
                        navController.navigate("project_list") {
                            popUpTo("home")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("View List", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
