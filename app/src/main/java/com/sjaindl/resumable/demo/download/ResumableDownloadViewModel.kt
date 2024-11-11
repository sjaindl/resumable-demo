package com.sjaindl.resumable.demo.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ketch.Ketch
import com.ketch.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.File

class ResumableDownloadViewModel: ViewModel() {

    private lateinit var ketch: Ketch
    private lateinit var baseDir: File

    private var _downloadStatus = MutableStateFlow(value = "")
    val downloadStatus = _downloadStatus.asStateFlow()

    private var _progress = MutableStateFlow(value = 0F)
    val progress = _progress.asStateFlow()

    private var _isPaused = MutableStateFlow(value = false)
    val isPaused = _isPaused.asStateFlow()

    private var downloadID: Int? = null

    fun init(baseDir: File, ketch: Ketch) {
        this.baseDir = baseDir
        this.ketch = ketch

        viewModelScope.launch {
            ketch.observeDownloads()
                .flowOn(Dispatchers.IO)
                .collect { models ->
                    val model = models.find {
                        it.id == downloadID
                    }

                    model?.let {
                        setDownloadStatus(status = it.status)
                        _progress.value = it.progress.toFloat()
                    }
                }
        }
    }

    fun beginDownload(uri: String) {
        val fileName = uri.substring(startIndex = uri.lastIndexOf(char = '/') + 1) + ".tmp"

        _isPaused.value = false
        downloadID = ketch.download(url = uri, path = baseDir.path, fileName = fileName)
    }

    fun pauseDownload() {
        downloadID?.let {
            ketch.pause(id = it)
            _isPaused.value = true
        }
    }

    fun resumeDownload() {
        downloadID?.let {
            ketch.resume(id = it)
            _isPaused.value = false
        }
    }

    private fun setDownloadStatus(status: Status) {
        _downloadStatus.value = "Downloaded ${progress.value} % | " + status.name
    }
}
