package com.synfusion.pipelistpro.features.settings

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.synfusion.pipelistpro.data.models.ThemeMode
import com.synfusion.pipelistpro.features.cart.ProjectViewModel
import com.synfusion.pipelistpro.ui.components.AppTopHeader

@Composable
fun SettingsScreen(viewModel: ProjectViewModel, navController: NavController) {
    val themeMode by viewModel.themeMode.collectAsState()
    val currentProject by viewModel.currentProject.collectAsState()
    val savedProjects by viewModel.savedProjects.collectAsState(emptyList())
    val context = LocalContext.current
    var confirmAction by remember { mutableStateOf<ConfirmAction?>(null) }
    var showAbout by remember { mutableStateOf(false) }

    confirmAction?.let { action ->
        AlertDialog(
            onDismissRequest = { confirmAction = null },
            icon = { Icon(action.icon, contentDescription = null) },
            title = { Text(action.title) },
            text = { Text(action.message) },
            confirmButton = {
                TextButton(
                    onClick = {
                        when (action) {
                            ConfirmAction.CLEAR_CURRENT -> viewModel.clearCurrentProject()
                            ConfirmAction.CLEAR_SAVED -> viewModel.clearSavedProjects()
                            ConfirmAction.CLEAR_ALL -> viewModel.clearAllLists()
                        }
                        confirmAction = null
                    }
                ) { Text("Clear") }
            },
            dismissButton = { TextButton(onClick = { confirmAction = null }) { Text("Cancel") } }
        )
    }

    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            icon = { Icon(Icons.Default.Plumbing, contentDescription = null) },
            title = { Text("About PipeList Pro") },
            text = { Text("Offline plumbing material list app for quickly preparing, saving, and sharing project-wise material lists.") },
            confirmButton = { TextButton(onClick = { showAbout = false }) { Text("OK") } }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item { AppTopHeader(title = "Settings", subtitle = "App preferences and list controls") }

        item {
            Column(modifier = Modifier.padding(24.dp)) {
                SectionLabel("Theme")
                SettingsCard {
                    ThemeMode.entries.forEachIndexed { index, mode ->
                        SettingsRadioItem(
                            icon = when (mode) {
                                ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                                ThemeMode.LIGHT -> Icons.Default.LightMode
                                ThemeMode.DARK -> Icons.Default.DarkMode
                            },
                            title = when (mode) {
                                ThemeMode.SYSTEM -> "Use system theme"
                                ThemeMode.LIGHT -> "Light theme"
                                ThemeMode.DARK -> "Dark theme"
                            },
                            selected = themeMode == mode,
                            onClick = { viewModel.setThemeMode(mode) }
                        )
                        if (index != ThemeMode.entries.lastIndex) SettingsDivider()
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                SectionLabel("List Management")
                SettingsCard {
                    SettingsActionItem(
                        icon = Icons.Default.CleaningServices,
                        title = "Clear current list",
                        subtitle = if (currentProject == null) "No active list" else "Remove active in-progress list",
                        enabled = currentProject != null,
                        onClick = { confirmAction = ConfirmAction.CLEAR_CURRENT }
                    )
                    SettingsDivider()
                    SettingsActionItem(
                        icon = Icons.Default.FolderDelete,
                        title = "Clear saved lists",
                        subtitle = "${savedProjects.size} saved ${if (savedProjects.size == 1) "list" else "lists"}",
                        enabled = savedProjects.isNotEmpty(),
                        onClick = { confirmAction = ConfirmAction.CLEAR_SAVED }
                    )
                    SettingsDivider()
                    SettingsActionItem(
                        icon = Icons.Default.DeleteForever,
                        title = "Clear all data",
                        subtitle = "Current and saved lists",
                        enabled = currentProject != null || savedProjects.isNotEmpty(),
                        onClick = { confirmAction = ConfirmAction.CLEAR_ALL }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                SectionLabel("More")
                SettingsCard {
                    SettingsActionItem(
                        icon = Icons.Default.Share,
                        title = "Share app",
                        subtitle = "Tell another plumber about PipeList Pro",
                        onClick = {
                            val text = "Try PipeList Pro for offline plumbing material lists."
                            context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, text)
                            }, "Share PipeList Pro"))
                        }
                    )
                    SettingsDivider()
                    SettingsActionItem(icon = Icons.Default.Info, title = "About PipeList Pro", onClick = { showAbout = true })
                }

                Spacer(modifier = Modifier.height(24.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Version 1.0.0", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

private enum class ConfirmAction(val title: String, val message: String, val icon: ImageVector) {
    CLEAR_CURRENT("Clear current list?", "This removes the active in-progress list from this device.", Icons.Default.CleaningServices),
    CLEAR_SAVED("Clear saved lists?", "This removes all saved project lists. This cannot be undone.", Icons.Default.FolderDelete),
    CLEAR_ALL("Clear all data?", "This removes the current list and all saved lists. This cannot be undone.", Icons.Default.DeleteForever)
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) { Column { content() } }
}

@Composable
private fun SettingsRadioItem(icon: ImageVector, title: String, selected: Boolean, onClick: () -> Unit) {
    Surface(onClick = onClick, color = Color.Transparent) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, style = MaterialTheme.typography.bodyLarge)
            }
            RadioButton(selected = selected, onClick = onClick)
        }
    }
}

@Composable
fun SettingsActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    Surface(onClick = onClick, enabled = enabled, color = Color.Transparent) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            val color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f))
                if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
}
