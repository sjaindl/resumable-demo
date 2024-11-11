package com.sjaindl.resumable.demo

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sjaindl.resumable.demo.download.ResumableDownloadDemoActivity
import com.sjaindl.resumable.demo.upload.ResumableUploadDemoActivity
import com.sjaindl.resumable.ui.theme.ResumableDemoAppTheme

@Composable
fun DemoScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedButton(
            onClick = {
                val intent = Intent(context, ResumableUploadDemoActivity::class.java)
                context.startActivity(intent)
            }
        ) {
            Text(text = "Resumable Upload Demo")
        }

        OutlinedButton(
            onClick = {
                val intent = Intent(context, ResumableDownloadDemoActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Resumable Download Demo")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DemoScreenPreview() {
    ResumableDemoAppTheme {
        DemoScreen()
    }
}
