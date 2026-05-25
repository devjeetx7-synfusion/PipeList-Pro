package com.synfusion.pipelistpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.synfusion.pipelistpro.ui.components.AppTopHeader
import com.synfusion.pipelistpro.ui.components.SectionTitle
import com.synfusion.pipelistpro.ui.theme.PipeListProTheme

@Composable
fun SettingsScreen(viewModel: com.synfusion.pipelistpro.viewmodel.ProjectViewModel, navController: NavController) {
    var plumberName by remember { mutableStateOf("WorkDaily Hub") }
    var email by remember { mutableStateOf("plumbing.pro@example.com") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            AppTopHeader(
                title = "Settings",
                subtitle = "App preferences and profile"
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            Icons.Default.Build,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Profile Settings", style = MaterialTheme.typography.titleLarge)

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = plumberName,
                        onValueChange = { plumberName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                    )
                }
            }
        }

        item {
            SectionTitle(title = "App Preferences", modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp))

            PreferenceItem(icon = Icons.Default.Palette, title = "Theme", value = "Light")
            PreferenceItem(icon = Icons.Default.Notifications, title = "Notifications", value = "Enabled")
            PreferenceItem(icon = Icons.Default.Info, title = "About PipeList Pro", value = "v1.0.0")
        }
    }
}

@Composable
fun PreferenceItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
            }
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    PipeListProTheme {
        SettingsScreen(
            viewModel = com.synfusion.pipelistpro.viewmodel.ProjectViewModel(android.app.Application()),
            navController = rememberNavController()
        )
    }
}
