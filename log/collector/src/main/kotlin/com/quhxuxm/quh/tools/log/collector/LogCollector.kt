package com.quhxuxm.quh.tools.log.collector

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
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

    private suspend fun unzip(zipFilePath: Path, outputFilePath: Path) = withContext(unzipLogFileIoContext) {
        println("Unzip thread: ${Thread.currentThread().name}")
        println("Begin to unzip file: ${zipFilePath}")
        val gzipFileInputStream = GZIPInputStream(FileInputStream(zipFilePath.toFile()))
        Files.copy(gzipFileInputStream, outputFilePath, StandardCopyOption.REPLACE_EXISTING)
        gzipFileInputStream.close()
        println("Success to unzip: ${zipFilePath.toAbsolutePath()} to file: ${outputFilePath.toAbsolutePath()}")
    }

    private suspend fun downloadRemoteFile(remoteFileUrlString: String): Path? = coroutineScope {
        println("Collect log thread: ${Thread.currentThread().name}")
        val randomFileName = UUID.randomUUID().toString().replace("-", "")
        val downloadPath = Path.of(TMP_FOLDER, randomFileName)
        val remoteLogFileInputStream = try {
            val remoteFileUrl = URL(remoteFileUrlString)
            remoteFileUrl.openStream()
        } catch (e: IOException) {
            println("Fail to open remote log file input stream: ${remoteFileUrlString} because of exception.")
            e.printStackTrace()
            return@coroutineScope null
        }
        try {
            println("Begin to download file: ${remoteFileUrlString}")
            val downloadResult = async(collectRemoteFileIoContext) {
                Files.copy(remoteLogFileInputStream, downloadPath, StandardCopyOption.REPLACE_EXISTING)
                return@async downloadPath
            }
            println("Success to download file: ${remoteFileUrlString}")
            return@coroutineScope downloadResult.await()
        } catch (e: Exception) {
            println("Fail to download remote log file: ${remoteFileUrlString} because of exception.")
            e.printStackTrace()
            return@coroutineScope null
        } finally {
            remoteLogFileInputStream.close()
        }
    }

    suspend fun collectLog(dataCenter: DataCenter, logFileCategory: LogFileCategory,
                           stack: AppStack = AppStack.A, date: Date, targetBaseFolderPath: String,
                           indexRange: IntRange = 1..4) = withContext(collectRemoteFileIoContext) {
        val dataFormat = SimpleDateFormat("yyyy-MM-dd")
        val dataSuffix = dataFormat.format(date)
        val targetFolderPath = Path.of(targetBaseFolderPath, dataCenter.id, logFileCategory.id)
        if (!targetFolderPath.toFile().exists()) {
            targetFolderPath.toFile().mkdirs()
        }
        indexRange.forEach {
            val appIndex = String.format("%02d", it)
            val downloadFilePath = downloadRemoteFile(
                    "$LOG_SERVER_BASE_URL/${dataCenter.id}/${logFileCategory.id}/${dataCenter.shortName}prod/" +
                            "${dataCenter.shortName}-${logFileCategory.serverName}${appIndex}${stack.id}/${logFileCategory.logFile}.${dataSuffix}.gz")
            val unzipFileResultPath = Path.of(targetFolderPath.toString(),
                    "${logFileCategory.logFile}.${dataSuffix}.${logFileCategory.serverName}${appIndex}.log")
            if (downloadFilePath == null) {
                return@forEach
            }
            unzip(downloadFilePath, unzipFileResultPath)
        }
    }
}