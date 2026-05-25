package com.synfusion.pipelistpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.synfusion.pipelistpro.ui.components.AppTopHeader
import com.synfusion.pipelistpro.ui.components.StatCard
import com.synfusion.pipelistpro.ui.theme.PipeListProTheme

@Composable
fun StatisticsScreen(viewModel: com.synfusion.pipelistpro.viewmodel.ProjectViewModel, navController: NavController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val savedProjects by viewModel.savedProjects.observeAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopHeader(
            title = "Statistics",
            subtitle = "Overview of your plumbing work"
        )

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Monthly", modifier = Modifier.padding(16.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("All-time", modifier = Modifier.padding(16.dp))
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        title = "Projects Done",
                        value = savedProjects.size.toString(),
                        icon = Icons.Default.Timeline,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Items Used",
                        value = savedProjects.sumOf { it.items.size }.toString(),
                        icon = Icons.Default.PieChart,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ShowChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Work Growth Chart",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Visual data representation coming soon",
                            style = MaterialTheme.typography.bodySmall,
                            color = com.synfusion.pipelistpro.ui.theme.SlateGraySubtitle
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsScreenPreview() {
    PipeListProTheme {
        StatisticsScreen(
            viewModel = com.synfusion.pipelistpro.viewmodel.ProjectViewModel(android.app.Application()),
            navController = rememberNavController()
        )
    }
}
