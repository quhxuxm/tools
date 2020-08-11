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
import kotlin.collections.HashSet

private data class RemoteFileDownloadInfo(val remoteFileUrlString: String, val downloadSuccess: Boolean,
                                          val downloadZipFilePath: Path)

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
                           stack: AppStack = AppStack.A, date: Date, logDownloadPath: Path,
                           indexRange: IntRange = 1..4): Set<Path> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateSuffix = dateFormat.format(date)
        val targetFolderPath = logDownloadPath.resolve(dataCenter.id).resolve(logFileCategory.id)
        if (!targetFolderPath.toFile().exists()) {
            targetFolderPath.toFile().mkdirs()
        }
        val result = HashSet<Path>()
        indexRange.forEach {
            val appIndex = String.format("%02d", it)
            val finalFileName = "${logFileCategory.logFile}.${dateSuffix}.${logFileCategory.serverName}${appIndex}.log"
            val downloadZipFileName = "${finalFileName}.gz"
            val unzipFileResultPath = Path.of(targetFolderPath.toString(), finalFileName)

            coroutineScope {
                if (unzipFileResultPath.toFile().exists()) {
                    logger.info("Unzip already: $unzipFileResultPath")
                    result.add(unzipFileResultPath)
                    return@coroutineScope
                }
                val downloadZipFilePath = Path.of(TMP_FOLDER, downloadZipFileName)
                val downloadInfoDeferred = async(collectRemoteFileIoContext) {
                    val remoteFileUrlString =
                            "$LOG_SERVER_BASE_URL/${dataCenter.id}/${logFileCategory.id}/${dataCenter.shortName}prod/" +
                                    "${dataCenter.shortName}-${logFileCategory.serverName}${appIndex}${stack.id}/${logFileCategory.logFile}.${dateSuffix}.gz"
                    if (downloadZipFilePath.toFile().exists()) {
                        logger.info("Download already: $remoteFileUrlString")
                        return@async RemoteFileDownloadInfo(
                                remoteFileUrlString = remoteFileUrlString,
                                downloadSuccess = true,
                                downloadZipFilePath = downloadZipFilePath
                        )
                    }
                    try {
                        downloadRemoteFile(remoteFileUrlString, downloadZipFilePath)
                    } catch (e: Exception) {
                        return@async RemoteFileDownloadInfo(
                                remoteFileUrlString = remoteFileUrlString,
                                downloadSuccess = false,
                                downloadZipFilePath = downloadZipFilePath
                        )
                    }
                    return@async RemoteFileDownloadInfo(
                            remoteFileUrlString = remoteFileUrlString,
                            downloadSuccess = true,
                            downloadZipFilePath = downloadZipFilePath
                    )
                }

                launch(unzipLogFileIoContext) {
                    val downloadInfo = downloadInfoDeferred.await();
                    if (!downloadInfo.downloadSuccess) {
                        return@launch
                    }
                    if (unzipFileResultPath.toFile().exists()) {
                        logger.info("Unzip already: $unzipFileResultPath")
                        result.add(unzipFileResultPath)
                        return@launch
                    }
                    unzip(downloadZipFilePath, unzipFileResultPath)
                    result.add(unzipFileResultPath)
                }
            }
        }
        return result
    }
}
