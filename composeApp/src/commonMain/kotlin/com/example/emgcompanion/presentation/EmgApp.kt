package com.example.emgcompanion.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun EmgApp(viewModel: EmgViewModel) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF62E0CC),
            onPrimary = Color(0xFF062B2B),
            secondary = Color(0xFF90B8FF),
            background = Color(0xFF091419),
            surface = Color(0xFF112229),
            surfaceVariant = Color(0xFF1B3038),
            onBackground = Color(0xFFE8F1F2),
            onSurface = Color(0xFFE8F1F2),
            onSurfaceVariant = Color(0xFFA2B4B8),
            error = Color(0xFFFF8A80),
        ),
    ) {
        EmgScreen(viewModel)
    }
}
