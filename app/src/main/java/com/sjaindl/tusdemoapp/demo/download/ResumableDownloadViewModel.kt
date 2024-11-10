package com.sjaindl.tusdemoapp.demo.download

import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class ResumableDownloadViewModel: ViewModel() {

    private lateinit var downloadManager: DownloadManager
    private lateinit var baseDir: File

    private var fileUri: String? = null

    private var _downloadStatus = MutableStateFlow(value = "")
    val downloadStatus = _downloadStatus.asStateFlow()

    private var _progress = MutableStateFlow(value = 0F)
    val progress = _progress.asStateFlow()

    private var _isPaused = MutableStateFlow(value = false)
    val isPaused = _isPaused.asStateFlow()

    private var _error = MutableStateFlow<String?>(value = null)
    val error = _error.asStateFlow()

    private var downloadID: Long? = null

    fun init(baseDir: File, downloadManager: DownloadManager) {
        this.baseDir = baseDir
        this.downloadManager = downloadManager
    }

    fun beginDownload(uri: String) {
        fileUri = uri

        viewModelScope.launch(Dispatchers.IO) {
            _isPaused.value = false
            downloadFile(url = uri)
        }
    }

    fun pauseDownload() {
        downloadID?.let { id ->
            downloadManager.remove(id)
            _isPaused.value = true
        }
    }

    fun resumeDownload() {
        fileUri?.let {
            viewModelScope.launch(Dispatchers.IO) {
                _isPaused.value = false
                downloadFile(url = it)
            }
        }
    }

    fun onErrorDismissed() {
        _error.value = null
    }

    private fun downloadFile(url: String) {
        val fileName = url.substring(startIndex = url.lastIndexOf(char = '/') + 1) + ".tmp"
        val file = createTempFile(fileName)

        val request = DownloadManager.Request(Uri.parse(url))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationUri(Uri.fromFile(file))
            .setTitle(fileName)
            .setDescription("Downloading file")
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val existingFileSize = file.length()
        request.addRequestHeader("Range", "bytes=$existingFileSize-")

        val id = downloadManager.enqueue(request) // enqueues the download request in the queue
        downloadID = id

        downloadWithId(id = id)
    }

    private fun downloadWithId(id: Long) {
        var finishDownload = false
        while (!finishDownload) {
            val cursor = downloadManager.query(DownloadManager.Query().setFilterById(id))
            if (cursor.moveToFirst()) {
                val status = with(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) {
                    cursor.getInt(this)
                }

                setDownloadStatus(status = status)

                when (status) {
                    DownloadManager.STATUS_FAILED -> {
                        val reason = with(cursor.getColumnIndex(DownloadManager.COLUMN_REASON)) {
                            cursor.getString(this)
                        }
                        _error.value = reason
                        finishDownload = true
                    }

                    DownloadManager.STATUS_PAUSED -> { }
                    DownloadManager.STATUS_PENDING -> { }
                    DownloadManager.STATUS_RUNNING -> {
                        val totalBytes = with(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)) {
                            cursor.getLong(this)
                        }

                        if (totalBytes >= 0) {
                            val downloadedBytes = with(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)) {
                                cursor.getLong(this)
                            }

                            _progress.value = downloadedBytes.toFloat() / totalBytes.toFloat() * 100
                        }
                    }

                    DownloadManager.STATUS_SUCCESSFUL -> {
                        _progress.value = 100f
                        setDownloadStatus(status = status)

                        finishDownload = true
                    }
                }
            }
        }
    }

    private fun createTempFile(fileName: String): File {
        return File(baseDir, fileName)
    }

    private fun setDownloadStatus(status: Int) {
        _downloadStatus.value = "Downloaded ${progress.value} % | " + when(status) {
            DownloadManager.STATUS_FAILED -> "failed ($status)"
            DownloadManager.STATUS_PAUSED -> "paused ($status)"
            DownloadManager.STATUS_PENDING -> "pending ($status)"
            DownloadManager.STATUS_RUNNING -> "running ($status)"
            DownloadManager.STATUS_SUCCESSFUL -> "successful ($status)"
            else -> "status $status"
        }
    }
}
