package com.synfusion.pipelistpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.synfusion.pipelistpro.ui.components.*
import com.synfusion.pipelistpro.ui.theme.PipeListProTheme

@Composable
fun HomeScreen(viewModel: com.synfusion.pipelistpro.viewmodel.ProjectViewModel, navController: NavController) {
    val savedProjects by viewModel.savedProjects.observeAsState(emptyList())

    val totalItems = savedProjects.sumOf { it.items.size }
    val totalProjects = savedProjects.size

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            AppTopHeader(
                title = "Hello, Plumber",
                subtitle = "Manage your plumbing material lists"
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Total Projects",
                    value = totalProjects.toString(),
                    icon = Icons.Default.Folder,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total Items",
                    value = totalItems.toString(),
                    icon = Icons.Default.Inventory,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            PaddingValues(horizontal = 20.dp, vertical = 24.dp).let {
                PrimarySoftButton(
                    text = "View Material List",
                    onClick = { navController.navigate("project_list") },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
                )
            }
        }

        item {
            SectionTitle(
                title = "Quick Actions",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Add Material",
                    icon = Icons.Default.Add,
                    onClick = { navController.navigate("material") },
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "New Project",
                    icon = Icons.Default.CreateNewFolder,
                    onClick = {
                        viewModel.startNewProject()
                        navController.navigate("project_list")
                    },
                    modifier = Modifier.weight(1f)
                )
                val context = LocalContext.current
                QuickActionCard(
                    title = "Export PDF",
                    icon = Icons.Default.PictureAsPdf,
                    onClick = {
                        savedProjects.firstOrNull()?.let { project ->
                            com.synfusion.pipelistpro.pdf.PdfGenerator.generateProjectPdf(context, project)?.let { file ->
                                com.synfusion.pipelistpro.utils.ShareUtils.sharePdfFile(context, file)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            SectionTitle(
                title = "Recent Projects",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        if (savedProjects.isEmpty()) {
            item {
                EmptyStateCard(message = "No projects yet. Start by creating one!")
            }
        } else {
            items(savedProjects.take(5)) { project ->
                ProjectSummaryCard(
                    projectName = project.projectName,
                    date = project.date,
                    itemCount = project.items.size,
                    onClick = {
                        viewModel.loadProject(project)
                        navController.navigate("project_list")
                    },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PipeListProTheme {
        HomeScreen(
            viewModel = com.synfusion.pipelistpro.viewmodel.ProjectViewModel(android.app.Application()),
            navController = rememberNavController()
        )
    }
}
