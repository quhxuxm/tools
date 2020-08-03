package com.quhxuxm.quh.tools.log.collector

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
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

data class RemoteFileDownloadInfo(val remoteFileUrlString: String, val downloadSuccess: Boolean, val downloadPath: Path)
object LogCollector {
    private const val TMP_FOLDER = "./tmp"
    private const val LOG_SERVER_BASE_URL = "http://sf-prod-arch01.corp.wagerworks.com/archives"
    private val collectRemoteFileIoContext = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
    private val unzipLogFileIoContext = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
    private val logger = LoggerFactory.getLogger(LogCollector::class.java)

    init {
        val tmpFolderPath = Path.of(TMP_FOLDER)
        if (!tmpFolderPath.toFile().exists()) {
            tmpFolderPath.toFile().mkdirs()
        }
    }

    private fun unzip(zipFilePath: Path, outputFilePath: Path) {
        logger.info("Begin to unzip file: {}", zipFilePath)
        val gzipFileInputStream = GZIPInputStream(FileInputStream(zipFilePath.toFile()))
        Files.copy(gzipFileInputStream, outputFilePath, StandardCopyOption.REPLACE_EXISTING)
        gzipFileInputStream.close()
        Files.delete(zipFilePath)
        logger.info("Success to unzip: {} to file: {}", zipFilePath, outputFilePath)
    }

    private fun downloadRemoteFile(remoteFileUrlString: String, downloadPath: Path) {
        logger.info("Begin to download file: {}", remoteFileUrlString)
        val remoteLogFileInputStream = try {
            val remoteFileUrl = URL(remoteFileUrlString)
            remoteFileUrl.openStream()
        } catch (e: IOException) {
            logger.error("Fail to open remote log file input stream: {}", remoteFileUrlString, e)
            throw e
        }
        try {
            Files.copy(remoteLogFileInputStream, downloadPath, StandardCopyOption.REPLACE_EXISTING)
            logger.info("Success to download file: {}", remoteFileUrlString)
            return
        } catch (e: Exception) {
            logger.error("Fail to download remote log file: {}", remoteFileUrlString, e)
            throw e
        } finally {
            remoteLogFileInputStream.close()
        }
    }

    suspend fun collectLog(dataCenter: DataCenter, logFileCategory: LogFileCategory,
                           stack: AppStack = AppStack.A, date: Date, targetBaseFolderPath: String,
                           indexRange: IntRange = 1..4) {
        val dataFormat = SimpleDateFormat("yyyy-MM-dd")
        val dataSuffix = dataFormat.format(date)
        val targetFolderPath = Path.of(targetBaseFolderPath, dataCenter.id, logFileCategory.id)
        if (!targetFolderPath.toFile().exists()) {
            targetFolderPath.toFile().mkdirs()
        }
        indexRange.forEach {
            val appIndex = String.format("%02d", it)
            val randomFileName = UUID.randomUUID().toString().replace("-", "")

            coroutineScope {
                val downloadPath = Path.of(TMP_FOLDER, randomFileName)
                val downloadInfoDeferred = async(collectRemoteFileIoContext) {
                    val remoteFileUrlString =
                            "$LOG_SERVER_BASE_URL/${dataCenter.id}/${logFileCategory.id}/${dataCenter.shortName}prod/" +
                                    "${dataCenter.shortName}-${logFileCategory.serverName}${appIndex}${stack.id}/${logFileCategory.logFile}.${dataSuffix}.gz"
                    try {
                        downloadRemoteFile(remoteFileUrlString, downloadPath)
                    } catch (e: Exception) {
                        return@async RemoteFileDownloadInfo(
                                remoteFileUrlString = remoteFileUrlString,
                                downloadSuccess = false,
                                downloadPath = downloadPath
                        )
                    }
                    return@async RemoteFileDownloadInfo(
                            remoteFileUrlString = remoteFileUrlString,
                            downloadSuccess = true,
                            downloadPath = downloadPath
                    )
                }

                launch(unzipLogFileIoContext) {
                    val downloadInfo = downloadInfoDeferred.await();
                    if (!downloadInfo.downloadSuccess) {
                        return@launch
                    }
                    val unzipFileResultPath = Path.of(targetFolderPath.toString(),
                            "${logFileCategory.logFile}.${dataSuffix}.${logFileCategory.serverName}${appIndex}.log")
                    unzip(downloadPath, unzipFileResultPath)
                }
            }
        }
    }
}
