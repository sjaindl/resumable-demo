package com.sjaindl.resumable.demo.download

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
import com.ketch.DownloadConfig
import com.ketch.Ketch
import com.ketch.NotificationConfig
import com.sjaindl.resumable.R
import com.sjaindl.resumable.ui.theme.ResumableDemoAppTheme

class ResumableDownloadDemoActivity: ComponentActivity() {

    private val viewModel: ResumableDownloadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val ketch = Ketch
            .builder()
            .setNotificationConfig(
                config = NotificationConfig(
                    enabled = true,
                    channelName = "download_demo",
                    channelDescription = "Resumable Download Demo Channel",
                    smallIcon = R.drawable.ic_launcher_foreground),
            )
            .setDownloadConfig(
                config = DownloadConfig(
                    connectTimeOutInMs = 20000L, //Default: 10000L
                    readTimeOutInMs = 15000L //Default: 10000L
                )
            )
            .build(context = this)

        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        viewModel.init(baseDir = directory, ketch = ketch)

        setContent {
            ResumableDemoAppTheme {

                val uploadStatus by viewModel.downloadStatus.collectAsState()
                val progress by viewModel.progress.collectAsState()
                val isPaused by viewModel.isPaused.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->

                    ResumableDownloadScreen(
                        uploadStatus = uploadStatus,
                        progress = progress,
                        isPaused = isPaused,
                        onPause = viewModel::pauseDownload,
                        onResume = viewModel::resumeDownload,
                        onDownload = viewModel::beginDownload,
                        modifier = Modifier
                            .padding(innerPadding),
                    )
                }
            }
        }
    }
}
