package com.sjaindl.tusdemoapp.demo.download

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
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sjaindl.tusdemoapp.ui.theme.TusDemoAppTheme

@Composable
fun ResumableDownloadScreen(
    uploadStatus: String,
    progress: Float,
    isPaused: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onDownload: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedFileUri by remember { mutableStateOf<String?>(null) }

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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Resumable file download demo using Ketch",
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = selectedFileUri.orEmpty(),
                onValueChange = {
                    selectedFileUri = it
                }
            )

            Spacer(modifier = Modifier.height(height = 8.dp))

            Button(
                onClick = {
                    selectedFileUri?.let {
                        onDownload(it)
                    }
                },
                enabled = selectedFileUri != null,
            ) {
                Text("Download")
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
fun ResumableDownloadScreenPreview() {
    TusDemoAppTheme {
        ResumableDownloadScreen(
            uploadStatus = "Status",
            progress = 0F,
            isPaused = false,
            onPause = { },
            onResume = { },
            onDownload = { },
        )
    }
}
