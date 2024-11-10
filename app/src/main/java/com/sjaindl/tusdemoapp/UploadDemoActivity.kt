package com.sjaindl.tusdemoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.sjaindl.tusdemoapp.ui.theme.TusDemoAppTheme

class UploadDemoActivity: ComponentActivity() {

    private val viewModel: UploadScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.init(getSharedPreferences("tus", 0))

        setContent {
            TusDemoAppTheme {

                val uploadStatus by viewModel.uploadStatus.collectAsState()
                val progress by viewModel.progress.collectAsState()
                val isPaused by viewModel.isPaused.collectAsState()
                val error  by viewModel.error.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    UploadScreen(
                        uploadStatus = uploadStatus,
                        progress = progress,
                        isPaused = isPaused,
                        onPause = viewModel::pauseUpload,
                        error = error,
                        onResume = {
                            viewModel.resumeUpload(context = this)
                        },
                        onUpload = {
                            viewModel.beginUpload(uri = it, context = this)
                        },
                        onErrorDialogDismissed = viewModel::onErrorDismissed,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
