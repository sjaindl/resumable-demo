package com.sjaindl.resumable.demo.upload

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.sjaindl.resumable.ui.theme.ResumableDemoAppTheme

class ResumableUploadDemoActivity: ComponentActivity() {

    private val viewModel: ResumableUploadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.init(getSharedPreferences("tus", 0))

        setContent {
            ResumableDemoAppTheme {
                val context = LocalContext.current
                val clipboardManager = LocalClipboardManager.current

                val uploadStatus by viewModel.uploadStatus.collectAsState()
                val progress by viewModel.progress.collectAsState()
                val isPaused by viewModel.isPaused.collectAsState()
                val error  by viewModel.error.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    ResumableUploadScreen(
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
                        onCopyUrl = {
                            viewModel.uploadUri?.toString()?.let {
                                clipboardManager.setText(AnnotatedString(it))

                                Toast.makeText(
                                    context,
                                    "URI copied!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
