package com.quhxuxm.quh.tools.log.collector

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
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
    private val collectLogFileContext = Executors.newFixedThreadPool(20).asCoroutineDispatcher()
    private val unzipLogFileContext = Executors.newFixedThreadPool(20).asCoroutineDispatcher()

    init {
        val tmpFolderPath = Path.of(TMP_FOLDER)
        if (!tmpFolderPath.toFile().exists()) {
            tmpFolderPath.toFile().mkdirs()
        }
    }

    private suspend fun unzip(zipFilePath: Path, outputFilePath: Path) = coroutineScope {
        launch {
            async(unzipLogFileContext) {
                println("Begin to unzip file: ${zipFilePath}")
                val gzipFileInputStream = GZIPInputStream(FileInputStream(zipFilePath.toFile()))
                Files.copy(gzipFileInputStream, outputFilePath, StandardCopyOption.REPLACE_EXISTING)
                gzipFileInputStream.close()
                println("Success to unzip: ${zipFilePath.toAbsolutePath()} to file: ${outputFilePath.toAbsolutePath()}")
            }
        }
    }

    suspend fun collect(logPath: String, targetPath: String) = coroutineScope {
        val resultFilePath = Path.of(targetPath)
        val randomFileName = UUID.randomUUID().toString().replace("-", "")
        val downloadPath = Path.of(TMP_FOLDER, randomFileName)

        launch {
            val isDownloadDone = async(collectLogFileContext) {
                val remoteLogFileInputStream = try {
                    val remoteLogUrl = URL(logPath)
                    remoteLogUrl.openStream()
                } catch (e: IOException) {
                    println("Fail to open remote log file input stream: ${logPath} because of exception.")
                    e.printStackTrace()
                    return@async true
                }
                try {
                    println("Begin to download file: ${logPath}")
                    Files.copy(remoteLogFileInputStream, downloadPath, StandardCopyOption.REPLACE_EXISTING)
                    println("Success to download file: ${logPath}")
                    return@async true
                } catch (e: Exception) {
                    println("Fail to download remote log file: ${logPath} because of exception.")
                    e.printStackTrace()
                    return@async true
                } finally {
                    remoteLogFileInputStream.close()
                }
            }
            if (isDownloadDone.await()) {
                this@LogCollector.unzip(downloadPath, resultFilePath)
            }
        }
    }

    suspend fun collectComponentLog(dataCenter: DataCenter, component: Component,
                                    stack: AppStack = AppStack.A, date: Date, targetBaseFolderPath: String,
                                    indexRange: IntRange = 1..4) {
        val dataFormat = SimpleDateFormat("yyyy-MM-dd")
        val dataSuffix = dataFormat.format(date)
        val targetFolderPath = Path.of(targetBaseFolderPath, dataCenter.id, component.id)
        if (!targetFolderPath.toFile().exists()) {
            targetFolderPath.toFile().mkdirs()
        }
        indexRange.forEach {
            val appIndex = String.format("%02d", it)
            component.logFiles.forEach { logFileName ->
                collect(
                        "$LOG_SERVER_BASE_URL/${dataCenter.id}/${component.id}/${dataCenter.shortName}prod/" +
                                "${dataCenter.shortName}-${component.serverName}${appIndex}${stack.id}/${logFileName}.${dataSuffix}.gz",
                        Path.of(targetFolderPath.toString(),
                                "${logFileName}.${dataSuffix}.${component.serverName}${appIndex}.log")
                                .toString())
            }
        }
    }
}