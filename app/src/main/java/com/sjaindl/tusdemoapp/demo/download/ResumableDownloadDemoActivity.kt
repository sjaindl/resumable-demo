package com.sjaindl.tusdemoapp.demo.download

import android.app.DownloadManager
import android.os.Bundle
import android.os.Environment
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

class ResumableDownloadDemoActivity: ComponentActivity() {

    private val viewModel: ResumableDownloadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        viewModel.init(baseDir = directory, downloadManager = downloadManager)

        setContent {
            TusDemoAppTheme {

                val uploadStatus by viewModel.downloadStatus.collectAsState()
                val progress by viewModel.progress.collectAsState()
                val isPaused by viewModel.isPaused.collectAsState()
                val error  by viewModel.error.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->

                    ResumableDownloadScreen(
                        uploadStatus = uploadStatus,
                        progress = progress,
                        isPaused = isPaused,
                        onPause = viewModel::pauseDownload,
                        error = error,
                        onResume = viewModel::resumeDownload,
                        onDownload = {
                            viewModel.beginDownload(uri = it)
                        },
                        onErrorDialogDismissed = viewModel::onErrorDismissed,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
