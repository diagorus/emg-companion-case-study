package com.example.emgcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.emgcompanion.data.createDefaultMeasurementRepository
import com.example.emgcompanion.presentation.EmgApp
import com.example.emgcompanion.presentation.EmgViewModel

class MainActivity : ComponentActivity() {
    private val viewModel by lazy {
        ViewModelProvider(this, EmgViewModelFactory())[EmgViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EmgApp(viewModel)
        }
    }
}

private class EmgViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(EmgViewModel::class.java))
        return EmgViewModel(createDefaultMeasurementRepository()) as T
    }
}
