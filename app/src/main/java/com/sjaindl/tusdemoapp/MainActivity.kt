package com.sjaindl.tusdemoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.sjaindl.tusdemoapp.demo.DemoScreen
import com.sjaindl.tusdemoapp.ui.theme.TusDemoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TusDemoAppTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    DemoScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
