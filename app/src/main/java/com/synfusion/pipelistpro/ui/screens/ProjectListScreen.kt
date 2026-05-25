package com.synfusion.pipelistpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.synfusion.pipelistpro.ui.components.*
import com.synfusion.pipelistpro.ui.theme.PipeListProTheme

@Composable
fun ProjectListScreen(viewModel: com.synfusion.pipelistpro.viewmodel.ProjectViewModel, navController: NavController) {
    val currentProject by viewModel.currentProject.observeAsState()

    Scaffold(
        floatingActionButton = {
            val context = LocalContext.current
            if (currentProject != null && currentProject!!.items.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            currentProject?.let { project ->
                                com.synfusion.pipelistpro.pdf.PdfGenerator.generateProjectPdf(context, project)?.let { file ->
                                    com.synfusion.pipelistpro.utils.ShareUtils.sharePdfFile(context, file)
                                }
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share PDF")
                    }

                    FloatingActionButton(
                        onClick = {
                            currentProject?.let { project ->
                                com.synfusion.pipelistpro.utils.ShareUtils.shareProjectAsText(context, project)
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy List")
                    }

                    ExtendedFloatingActionButton(
                        onClick = { viewModel.saveCurrentProject() },
                        icon = { Icon(Icons.Default.Save, contentDescription = null) },
                        text = { Text("Save") },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = MaterialTheme.shapes.extraLarge
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AppTopHeader(
                title = currentProject?.projectName ?: "New Project",
                subtitle = currentProject?.date ?: "Project Items",
                navigationIcon = Icons.Default.ArrowBack,
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
                    modifier = Modifier.padding(20.dp)
                )
            } else {
                val groupedItems = currentProject!!.items.groupBy { it.category }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp)
                ) {
                    groupedItems.forEach { (category, items) ->
                        item {
                            SectionTitle(title = category)
                        }
                        items(items) { item ->
                            MaterialItemCard(
                                name = item.materialName,
                                category = item.category,
                                size = item.size,
                                quantity = item.quantity,
                                onQuantityChange = { newQty ->
                                    viewModel.updateItemQuantityByItem(item, newQty)
                                },
                                onAddClick = { /* No-op in list view or use for something else */ },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Items",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = currentProject!!.items.sumOf { it.quantity }.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProjectListScreenPreview() {
    PipeListProTheme {
        ProjectListScreen(
            viewModel = com.synfusion.pipelistpro.viewmodel.ProjectViewModel(android.app.Application()),
            navController = rememberNavController()
        )
    }
}
