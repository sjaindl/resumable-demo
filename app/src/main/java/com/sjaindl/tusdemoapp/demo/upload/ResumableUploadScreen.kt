package com.sjaindl.tusdemoapp.demo.upload

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sjaindl.tusdemoapp.ui.theme.TusDemoAppTheme

@Composable
fun ResumableUploadScreen(
    uploadStatus: String,
    progress: Float,
    isPaused: Boolean,
    error: Throwable?,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onUpload: (Uri) -> Unit,
    onErrorDialogDismissed: () -> Unit,
    onCopyUrl: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            val selectedFileUri = intent?.data

            selectedFileUri?.let { uri ->
                onUpload(uri)
            }
        }
    }

    error?.let {
        AlertDialog(
            onDismissRequest = onErrorDialogDismissed,
            title = { Text(text = "Upload error") },
            text = { Text(text = error.message ?: "An error occurred") },
            confirmButton = {
                Button(
                    onClick = onErrorDialogDismissed,
                ) {
                    Text(text = "OK")
                }
            },
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp,
            ),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Resumable file upload demo using TUS",
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = "*/*" // Allow any file type
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
                launcher.launch(intent)

            }) {
                Text("Upload")
            }

            Spacer(modifier = Modifier.height(height = 8.dp))

            Text(
                text = uploadStatus,
                modifier = Modifier.clickable {
                    onCopyUrl()
                }
            )

            Spacer(modifier = Modifier.height(height = 8.dp))

            LinearProgressIndicator(
                progress = {
                    progress / 100f
                }
            )

            Spacer(modifier = Modifier.height(height = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onPause,
                    enabled = !isPaused
                ) {
                    Text(text = "Pause")
                }

                Spacer(modifier = Modifier.width(width = 8.dp))

                Button(
                    onClick = onResume,
                    enabled = isPaused,
                ) {
                    Text(text = "Resume")
                }
            }
        }
    }
}

@Preview
@Composable
fun UploadScreenPreview() {
    TusDemoAppTheme {
        ResumableUploadScreen(
            uploadStatus = "Status",
            progress = 0F,
            isPaused = false,
            error = null,
            onPause = { },
            onResume = { },
            onUpload = { },
            onCopyUrl = { },
            onErrorDialogDismissed = { },
        )
    }
}
