package com.quhxuxm.quh.tools.log.collector

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.zip.GZIPInputStream

object LogCollector {
    private const val TMP_FOLDER = "./tmp"
    private const val LOG_SERVER_BASE_URL = "http://sf-prod-arch01.corp.wagerworks.com/archives"
    private val collectRemoteFileIoContext = Executors.newFixedThreadPool(20).asCoroutineDispatcher()
    private val unzipLogFileIoContext = Executors.newFixedThreadPool(20).asCoroutineDispatcher()

    init {
        val tmpFolderPath = Path.of(TMP_FOLDER)
        if (!tmpFolderPath.toFile().exists()) {
            tmpFolderPath.toFile().mkdirs()
        }
    }

    private suspend fun unzip(zipFilePath: Path, outputFilePath: Path): Boolean = coroutineScope {
        var success = false;
        launch(unzipLogFileIoContext) {
            println("Unzip thread: ${Thread.currentThread().name}")
            println("Begin to unzip file: ${zipFilePath}")
            val gzipFileInputStream = GZIPInputStream(FileInputStream(zipFilePath.toFile()))
            Files.copy(gzipFileInputStream, outputFilePath, StandardCopyOption.REPLACE_EXISTING)
            gzipFileInputStream.close()
            Files.delete(zipFilePath)
            success = true
            println("Success to unzip: ${zipFilePath.toAbsolutePath()} to file: ${outputFilePath.toAbsolutePath()}")
        }
        return@coroutineScope success
    }

    private suspend fun downloadRemoteFile(remoteFileUrlString: String, downloadPath: Path): Boolean = coroutineScope {
        var success = false
        launch(collectRemoteFileIoContext) {
            println("Collect log thread: ${Thread.currentThread().name}")
            val remoteLogFileInputStream = try {
                val remoteFileUrl = URL(remoteFileUrlString)
                remoteFileUrl.openStream()
            } catch (e: IOException) {
                println("Fail to open remote log file input stream: ${remoteFileUrlString} because of exception.")
                e.printStackTrace()
                return@launch
            }
            try {
                println("Begin to download file: ${remoteFileUrlString}")
                Files.copy(remoteLogFileInputStream, downloadPath, StandardCopyOption.REPLACE_EXISTING)
                println("Success to download file: ${remoteFileUrlString}")
                success = true
                return@launch
            } catch (e: Exception) {
                println("Fail to download remote log file: ${remoteFileUrlString} because of exception.")
                e.printStackTrace()
                return@launch
            } finally {
                remoteLogFileInputStream.close()
            }
        }
        return@coroutineScope success
    }

    suspend fun collectLog(dataCenter: DataCenter, logFileCategory: LogFileCategory,
                           stack: AppStack = AppStack.A, date: Date, targetBaseFolderPath: String,
                           indexRange: IntRange = 1..4) = coroutineScope {
        val dataFormat = SimpleDateFormat("yyyy-MM-dd")
        val dataSuffix = dataFormat.format(date)
        val targetFolderPath = Path.of(targetBaseFolderPath, dataCenter.id, logFileCategory.id)
        if (!targetFolderPath.toFile().exists()) {
            targetFolderPath.toFile().mkdirs()
        }
        indexRange.forEach {
            val appIndex = String.format("%02d", it)
            val randomFileName = UUID.randomUUID().toString().replace("-", "")
            val downloadPath = Path.of(TMP_FOLDER, randomFileName)
            val success = downloadRemoteFile(
                    "$LOG_SERVER_BASE_URL/${dataCenter.id}/${logFileCategory.id}/${dataCenter.shortName}prod/" +
                            "${dataCenter.shortName}-${logFileCategory.serverName}${appIndex}${stack.id}/${logFileCategory.logFile}.${dataSuffix}.gz",
                    downloadPath)
            val unzipFileResultPath = Path.of(targetFolderPath.toString(),
                    "${logFileCategory.logFile}.${dataSuffix}.${logFileCategory.serverName}${appIndex}.log")

            if (!success) {
                return@forEach
            }
            unzip(downloadPath, unzipFileResultPath)
        }
    }
}