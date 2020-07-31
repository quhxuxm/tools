package com.quhxuxm.quh.tools.log.collector

import java.io.FileInputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.GZIPInputStream
import kotlin.concurrent.thread

object LogCollector {
    private const val TMP_FOLDER = "./tmp"
    private const val LOG_SERVER_BASE_URL = "http://sf-prod-arch01.corp.wagerworks.com/archives"

    init {
        val tmpFolderPath = Path.of(TMP_FOLDER)
        if (!tmpFolderPath.toFile().exists()) {
            tmpFolderPath.toFile().mkdirs()
        }
    }

    private fun unzip(zipFilePath: Path, outputFilePath: Path, callback: (resultFilePath: Path) -> Unit) {
        val gzipFileInputStream = GZIPInputStream(FileInputStream(zipFilePath.toFile()))
        Files.copy(gzipFileInputStream, outputFilePath, StandardCopyOption.REPLACE_EXISTING)
        gzipFileInputStream.close()
        println("Success to unzip: ${zipFilePath.toAbsolutePath()} to file: ${outputFilePath.toAbsolutePath()}")
        callback(outputFilePath)
    }

    fun collect(logPath: String, targetPath: String, callback: (resultFilePath: Path) -> Unit) {
        thread {
            try {
                println("Downloading file: ${logPath}")
                val remoteLogUrl = URL(logPath)
                val randomFileName = UUID.randomUUID().toString().replace("-", "")
                val downloadPath = Path.of(TMP_FOLDER, randomFileName)
                Files.copy(remoteLogUrl.openStream(), downloadPath, StandardCopyOption.REPLACE_EXISTING)
                val resultFilePath = Path.of(targetPath)
                println("Begin to unzip downloaded file: ${downloadPath}")
                thread {
                    try {
                        this.unzip(downloadPath, resultFilePath) {
                            Files.delete(downloadPath)
                            thread {
                                callback(resultFilePath)
                            }
                        }
                    } catch (e: Exception) {
                        println("Fail to unzip file: ${logPath} because of exception.")
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                println("Fail to collect file: ${logPath} because of exception.")
                e.printStackTrace()
            }
        }
    }

    fun collectComponentLog(dataCenter: DataCenter, component: Component,
                            stack: AppStack = AppStack.A, date: Date, targetBaseFolderPath: String,
                            indexRange: IntRange = 1..4,
                            callback: (resultFilePath: Path) -> Unit) {
        val dataFormat = SimpleDateFormat("yyyy-MM-dd")
        val dataSuffix = dataFormat.format(date)
        val targetFolderPath = Path.of(targetBaseFolderPath, dataCenter.id, component.id)
        if (!targetFolderPath.toFile().exists()) {
            targetFolderPath.toFile().mkdirs()
        }
        indexRange.forEach {
            collect(
                    "$LOG_SERVER_BASE_URL/${dataCenter.id}/${component.id}/${dataCenter.shortName}prod/" +
                            "${dataCenter.shortName}-${component.id}-app0${it}${stack.id}/${component.appLogFileName}.${dataSuffix}.gz",
                    Path.of(targetFolderPath.toString(),
                            "${component.appLogFileName}.${dataSuffix}.app0${it}.log")
                            .toString(),
                    callback)
        }
    }
}