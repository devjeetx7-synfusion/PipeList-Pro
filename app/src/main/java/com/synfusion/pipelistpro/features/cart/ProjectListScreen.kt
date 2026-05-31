package com.synfusion.pipelistpro.features.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.synfusion.pipelistpro.core.export.ImageExportManager
import com.synfusion.pipelistpro.core.export.PdfExportManager
import com.synfusion.pipelistpro.core.export.ShareManager
import com.synfusion.pipelistpro.data.models.CartItem
import com.synfusion.pipelistpro.ui.components.EmptyStateCard
import com.synfusion.pipelistpro.ui.components.PrimarySoftButton
import com.synfusion.pipelistpro.ui.components.QuantityStepper
import kotlinx.coroutines.launch

@Composable
fun CartItemCard(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    onUpdateDetails: (String, Double?, String) -> Unit = { _, _, _ -> }
) {
    var editExpanded by remember(item.id) { mutableStateOf(false) }
    var editSize by remember(item.id, item.size) { mutableStateOf(item.size) }
    var editFt by remember(item.id, item.ft) { mutableStateOf(item.ft?.let { formatNumber(it) }.orEmpty()) }
    var editUnit by remember(item.id, item.unit) { mutableStateOf(item.unit) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.32f)),
        shadowElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onRemove, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(19.dp))
                }

                Column(modifier = Modifier.weight(1f).padding(horizontal = 6.dp)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = itemMetaText(item),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                QuantityStepper(quantity = item.quantity, onQuantityChange = onQuantityChange)

                IconButton(onClick = { editExpanded = !editExpanded }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit item", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }
            }

            if (editExpanded) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = editSize,
                            onValueChange = { editSize = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Size") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp)
                        )
                        OutlinedTextField(
                            value = editUnit,
                            onValueChange = { editUnit = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Unit") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                    OutlinedTextField(
                        value = editFt,
                        onValueChange = { value -> editFt = value.filter { it.isDigit() || it == '.' }.take(8) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Length/ft (optional)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(14.dp)
                    )
                    Button(
                        onClick = {
                            onUpdateDetails(editSize, editFt.toDoubleOrNull(), editUnit)
                            editExpanded = false
                        },
                        modifier = Modifier.align(Alignment.End),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Apply") }
                }
            }
        }
    }
}

@Composable
private fun CartSummaryBottomBar(
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
        shadowElevation = 6.dp,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$itemCount ${if (itemCount == 1) "item" else "items"} • $totalQuantity qty",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$categoryCount ${if (categoryCount == 1) "category" else "categories"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(onClick = onSave, shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save")
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ExportActionChip(Icons.Default.PictureAsPdf, "PDF", onPdf, Modifier.weight(1f))
                ExportActionChip(Icons.Default.Image, "Image", onImage, Modifier.weight(1f))
                ExportActionChip(Icons.Default.Share, "Share", onText, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ExportActionChip(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ProjectDetailsEditor(projectName: String, notes: String, onChange: (String, String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = projectName,
            onValueChange = { onChange(it, notes) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("List name") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        OutlinedTextField(
            value = notes,
            onValueChange = { onChange(projectName, it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Notes (optional)") },
            minLines = 1,
            maxLines = 2,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreen(viewModel: ProjectViewModel, navController: NavController) {
    val currentProject by viewModel.currentProject.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isExporting by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Column {
                        Text("Material List", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, maxLines = 1)
                        Text(currentProject?.date.orEmpty(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    }
                },
                actions = {
                    TextButton(onClick = { navController.navigate("material") }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add")
                    }
                }
            )
        },
        bottomBar = {
            val project = currentProject
            if (project != null && project.items.isNotEmpty()) {
                Column {
                    if (isExporting) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    CartSummaryBottomBar(
                        itemCount = project.items.size,
                        totalQuantity = project.items.sumOf { it.quantity },
                        categoryCount = project.items.groupBy { it.category }.size,
                        onSave = {
                            viewModel.saveCurrentProject()
                            navController.popBackStack()
                        },
                        onPdf = {
                            if (isExporting) return@CartSummaryBottomBar
                            scope.launch {
                                isExporting = true
                                PdfExportManager.generatePdf(context, project)?.let { file ->
                                    ShareManager.shareFile(context, file, "application/pdf", "Share PDF")
                                } ?: snackbarHostState.showSnackbar("Failed to generate PDF")
                                isExporting = false
                            }
                        },
                        onImage = {
                            if (isExporting) return@CartSummaryBottomBar
                            scope.launch {
                                isExporting = true
                                ImageExportManager.generateImage(context, project)?.let { file ->
                                    ShareManager.shareFile(context, file, "image/png", "Share Image")
                                } ?: snackbarHostState.showSnackbar("Failed to generate Image")
                                isExporting = false
                            }
                        },
                        onText = { ShareManager.shareText(context, project) }
                    )
                }
            }
        }
    ) { paddingValues ->
        val project = currentProject
        if (project == null || project.items.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
                    .padding(20.dp)
            ) {
                EmptyStateCard(message = "Your list is empty. Add materials to get started!", modifier = Modifier.weight(1f))
                PrimarySoftButton(text = "Add Materials", onClick = { navController.navigate("material") })
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    ProjectDetailsEditor(
                        projectName = project.projectName,
                        notes = project.notes,
                        onChange = { name, notes -> viewModel.updateProjectDetails(name, notes) }
                    )
                }

                project.items.groupBy { it.category }.forEach { (category, items) ->
                    item(key = "header_$category") {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                        )
                    }
                    items(items, key = { it.id }) { item ->
                        CartItemCard(
                            item = item,
                            onQuantityChange = { newQty -> viewModel.updateCartItemQuantity(item.id, newQty) },
                            onRemove = {
                                viewModel.removeCartItem(item.id)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(message = "${item.name} removed", actionLabel = "Undo")
                                    if (result == SnackbarResult.ActionPerformed) viewModel.addItemToCurrentProject(item)
                                }
                            },
                            onUpdateDetails = { size, ft, unit -> viewModel.updateCartItem(item.id, size, ft, unit) }
                        )
                    }
                }
            }
        }
    }
}

private fun itemMetaText(item: CartItem): String {
    val details = buildList {
        if (item.size.isNotBlank()) add(item.size)
        item.ft?.let { add("${formatNumber(it)} ft") }
        add(item.category)
        add(item.unit)
    }
    return details.joinToString(" • ")
}

private fun formatNumber(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
