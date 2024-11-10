package com.sjaindl.tusdemoapp

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.tus.android.client.TusAndroidUpload
import io.tus.android.client.TusPreferencesURLStore
import io.tus.java.client.ProtocolException
import io.tus.java.client.TusClient
import io.tus.java.client.TusUpload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

class UploadScreenViewModel: ViewModel() {
    private val client by lazy {
        TusClient()
    }

    private var fileUri: Uri? = null
    private var uploadTask: Job? = null

    private var _uploadStatus = MutableStateFlow(value = "")
    val uploadStatus = _uploadStatus.asStateFlow()

    private var _progress = MutableStateFlow(value = 0F)
    val progress = _progress.asStateFlow()

    private var _isPaused = MutableStateFlow(value = false)
    val isPaused = _isPaused.asStateFlow()

    private var _error = MutableStateFlow<Exception?>(value = null)
    val error = _error.asStateFlow()

    fun init(preferences: SharedPreferences) {
        client.uploadCreationURL = URL("https://tusd.tusdemo.net/files/")
        client.enableResuming(TusPreferencesURLStore(preferences))
    }

    fun beginUpload(uri: Uri, context: Context) {
        fileUri = uri
        resumeUpload(context)
    }

    fun pauseUpload() {
        uploadTask?.cancel()
    }

    fun resumeUpload(context: Context) {
        val upload = TusAndroidUpload(fileUri, context)
        uploadFile(upload)
    }

    fun onErrorDismissed() {
        _error.value = null
    }

    private fun uploadFile(upload: TusUpload) {
        uploadTask = viewModelScope.launch {
            _uploadStatus.value = "Upload selected..."
            _progress.value = 0F
            _isPaused.value = false

            try {
                val uploadURL = withContext(Dispatchers.IO) {
                    performUpload(client = client, upload = upload, coroutineScope = this)
                }

                _uploadStatus.value = "Upload to $uploadURL finished!\n"
            } catch (e: IOException) {
                _error.value = e
            } catch (e: ProtocolException) {
                _error.value = e
            } finally {
                _isPaused.value = true
            }
        }
    }

    private suspend fun performUpload(
        client: TusClient,
        upload: TusUpload,
        coroutineScope: CoroutineScope,
    ): URL {
        val uploader = client.resumeOrCreateUpload(upload)
        val totalBytes = upload.size
        var uploadedBytes = 0L

        // Upload file in 1MiB chunks
        uploader.chunkSize = 1024 * 1024

        while (uploader.uploadChunk() > 0) {
            uploadedBytes = uploader.offset
            val progress = uploadedBytes.toFloat() / totalBytes.toFloat() * 100

            withContext(Dispatchers.Main) {
                _uploadStatus.value = "Uploaded $progress % | $uploadedBytes/$totalBytes"
                _progress.value = progress
            }
            // Check if the coroutine is cancelled
            coroutineScope.ensureActive()
        }

        uploader.finish()

        return uploader.uploadURL
    }
}
