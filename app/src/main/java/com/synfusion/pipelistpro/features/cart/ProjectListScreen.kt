package com.synfusion.pipelistpro.features.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.synfusion.pipelistpro.ui.components.*
import com.synfusion.pipelistpro.core.theme.PipeListProTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.graphics.SolidColor

import androidx.compose.ui.text.style.TextOverflow

@Composable
fun CartItemCard(
    item: com.synfusion.pipelistpro.data.models.CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                val metaText = if (item.unit == "ft" || item.ft != null) {
                    val ftDisplay = if (item.ft?.rem(1.0) == 0.0) item.ft.toInt().toString() else item.ft.toString()
                    "${item.size} • $ftDisplay ft • ${item.category}"
                } else {
                    "${item.size} • ${item.category}"
                }

                Text(
                    text = metaText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(2.dp)
            ) {
                IconButton(
                    onClick = { onQuantityChange(item.quantity - 1) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = item.quantity.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(
                    onClick = { onQuantityChange(item.quantity + 1) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }
            }

            val unitLabel = if (item.unit == "ft" || item.ft != null) "ft" else "pcs"
            Text(
                text = unitLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp).width(28.dp)
            )
        }
    }
}

@Composable
fun ProjectActionBar(
    itemCount: Int,
    totalQuantity: Int,
    categoryCount: Int,
    onSave: () -> Unit,
    onPdf: () -> Unit,
    onImage: () -> Unit,
    onText: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp,
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$itemCount Items • $totalQuantity Qty",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$categoryCount Categories",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = onSave,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save List")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionChip(
                    icon = Icons.Default.PictureAsPdf,
                    label = "PDF",
                    onClick = onPdf,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.secondary
                )
                ActionChip(
                    icon = Icons.Default.Image,
                    label = "Image",
                    onClick = onImage,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.tertiary
                )
                ActionChip(
                    icon = Icons.Default.Share,
                    label = "Share",
                    onClick = onText,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = color, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProjectListScreen(viewModel: com.synfusion.pipelistpro.features.cart.ProjectViewModel, navController: NavController) {
    val currentProject by viewModel.currentProject.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (currentProject != null && currentProject!!.items.isNotEmpty()) {
                ProjectActionBar(
                    itemCount = currentProject!!.items.size,
                    totalQuantity = currentProject!!.items.sumOf { it.quantity },
                    categoryCount = currentProject!!.items.groupBy { it.category }.size,
                    onSave = {
                        viewModel.saveCurrentProject()
                        navController.popBackStack()
                    },
                    onPdf = {
                        currentProject?.let { project ->
                            com.synfusion.pipelistpro.features.export.PdfGenerator.generateProjectPdf(navController.context, project)?.let { file ->
                                com.synfusion.pipelistpro.core.utils.ShareUtils.sharePdfFile(navController.context, file)
                            }
                        }
                    },
                    onImage = {
                        currentProject?.let { project ->
                            com.synfusion.pipelistpro.core.utils.ShareUtils.shareProjectAsImage(navController.context, project)
                        }
                    },
                    onText = {
                        currentProject?.let { project ->
                            com.synfusion.pipelistpro.core.utils.ShareUtils.shareProjectAsText(navController.context, project)
                        }
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .background(MaterialTheme.colorScheme.background)
        ) {
            AppTopHeader(
                title = currentProject?.projectName ?: "New List",
                subtitle = currentProject?.date ?: "Items",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = { navController.popBackStack() }
            )

            if (currentProject == null || currentProject!!.items.isEmpty()) {
                EmptyStateCard(
                    message = "Your list is empty. Add materials to get started!",
                    modifier = Modifier.weight(1f)
                )
                PrimarySoftButton(
                    text = "Add Materials",
                    onClick = { navController.navigate("material") },
                    modifier = Modifier.padding(24.dp)
                )
            } else {
                val groupedItems = currentProject!!.items.groupBy { it.category }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = paddingValues.calculateBottomPadding() + 24.dp
                    )
                ) {
                    groupedItems.forEach { (category, items) ->
                        item {
                            SectionTitle(title = category, modifier = Modifier.padding(vertical = 12.dp))
                        }
                        items(items) { item ->
                            CartItemCard(
                                item = item,
                                onQuantityChange = { newQty ->
                                    viewModel.updateCartItemQuantity(item.id, newQty)
                                },
                                onRemove = {
                                    viewModel.updateCartItemQuantity(item.id, 0)
                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "${item.name} removed",
                                            actionLabel = "Undo"
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            viewModel.addItemToCurrentProject(item)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
