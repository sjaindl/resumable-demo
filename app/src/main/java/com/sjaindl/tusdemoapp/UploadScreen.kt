package com.sjaindl.tusdemoapp

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sjaindl.tusdemoapp.ui.theme.TusDemoAppTheme

@Composable
fun UploadScreen(
    uploadStatus: String,
    progress: Float,
    isPaused: Boolean,
    error: Throwable?,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onUpload: (Uri) -> Unit,
    onErrorDialogDismissed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            selectedFileUri = intent?.data

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
            Text(text = "File uploading test for tus.io")

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

            Text(text = uploadStatus)

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
        UploadScreen(
            uploadStatus = "Status",
            progress = 0F,
            isPaused = false,
            error = null,
            onPause = { },
            onResume = { },
            onUpload = { },
            onErrorDialogDismissed = { },
        )
    }
}
