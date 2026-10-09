package com.example.emgcompanion

import androidx.compose.ui.window.ComposeUIViewController
import com.example.emgcompanion.data.createDefaultMeasurementRepository
import com.example.emgcompanion.presentation.EmgApp
import com.example.emgcompanion.presentation.EmgViewModel
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    EmgApp(EmgViewModel(createDefaultMeasurementRepository()))
}
