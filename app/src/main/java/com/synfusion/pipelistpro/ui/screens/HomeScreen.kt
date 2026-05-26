package com.synfusion.pipelistpro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.synfusion.pipelistpro.ui.components.*
import com.synfusion.pipelistpro.ui.theme.GradientEnd
import com.synfusion.pipelistpro.ui.theme.GradientStart
import com.synfusion.pipelistpro.viewmodel.ProjectViewModel

@Composable
fun HomeScreen(viewModel: ProjectViewModel, navController: NavController) {
    val savedProjects by viewModel.savedProjects.observeAsState(emptyList())
    val currentProject by viewModel.currentProject.observeAsState()

    val totalProjects = savedProjects.size

    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            HomeHeader()
        }

        item {
            DashboardStats(totalProjects, currentProject != null)
        }

        if (currentProject != null) {
            item {
                SectionTitle(title = "Current List", modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
                ProjectSummaryCard(
                    projectName = currentProject!!.projectName,
                    date = currentProject!!.date,
                    itemCount = currentProject!!.items.size,
                    onClick = { navController.navigate("project_list") },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        item {
            SectionTitle(title = "Quick Actions", modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickActionItem(
                    title = "Create",
                    icon = Icons.Default.AddCircle,
                    color = Color(0xFF2563EB),
                    onClick = {
                        viewModel.startNewProject()
                        navController.navigate("material")
                    },
                    modifier = Modifier.weight(1f)
                )
                QuickActionItem(
                    title = "Saved",
                    icon = Icons.Default.Folder,
                    color = Color(0xFF7C3AED),
                    onClick = { navController.navigate("project_list") },
                    modifier = Modifier.weight(1f)
                )
                QuickActionItem(
                    title = "Settings",
                    icon = Icons.Default.Settings,
                    color = Color(0xFF64748B),
                    onClick = { navController.navigate("settings") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            SectionTitle(title = "Recent Lists", modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
        }

        if (savedProjects.isEmpty()) {
            item {
                EmptyStateCard(message = "No material lists yet. Create your first list!")
            }
        } else {
            items(savedProjects) { project ->
                ProjectSummaryCard(
                    projectName = project.projectName,
                    date = project.date,
                    itemCount = project.items.size,
                    onClick = {
                        viewModel.loadProject(project)
                        navController.navigate("project_list")
                    },
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd)))
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                text = "PipeList Pro",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "Professional Plumbing Lists",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun DashboardStats(totalProjects: Int, hasCurrent: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .offset(y = (-30).dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatBox(
            label = "Saved Lists",
            value = totalProjects.toString(),
            icon = Icons.Default.Inventory,
            modifier = Modifier.weight(1f)
        )
        StatBox(
            label = "Status",
            value = if (hasCurrent) "In Progress" else "Ready",
            icon = if (hasCurrent) Icons.Default.Sync else Icons.Default.CheckCircle,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatBox(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun QuickActionItem(title: String, icon: ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            color = color.copy(alpha = 0.1f),
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(28.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
    }
}
