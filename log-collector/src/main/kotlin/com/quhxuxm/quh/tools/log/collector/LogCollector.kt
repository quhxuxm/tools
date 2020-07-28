package com.quhxuxm.quh.tools.log.collector

import java.io.FileInputStream
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
    private val collectFileIoExecutor = Executors.newFixedThreadPool(20)
    private val callbackExecutor = Executors.newFixedThreadPool(20)

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
        println("Success to unzip to file: ${outputFilePath.toAbsolutePath()}")
        callback(outputFilePath)
    }

    fun collect(logPath: String, targetPath: String, callback: (resultFilePath: Path) -> Unit) {
        collectFileIoExecutor.submit {
            try {
                println("Downloading file: ${logPath}")
                val remoteLogUrl = URL(logPath)
                val randomFileName = UUID.randomUUID().toString().replace("-", "")
                val downloadPath = Path.of(TMP_FOLDER, randomFileName)
                Files.copy(remoteLogUrl.openStream(), downloadPath, StandardCopyOption.REPLACE_EXISTING)
                val resultFilePath = Path.of(targetPath)
                println("Begin to unzip to file: ${targetPath}")
                callbackExecutor.submit {
                    this.unzip(downloadPath, resultFilePath, callback)
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

fun main() {
    val dataCenter = DataCenter.GIB
    val targetBaseFolderPath = "D:\\logs\\IIM-73824"
    val calendar = Calendar.getInstance();
    calendar.set(2020, Calendar.JULY, 26)
    val date1 = calendar.time
    calendar.set(2020, Calendar.JULY, 27)
    val date2 = calendar.time
    val dateToDownload = listOf(date1, date2)
    dateToDownload.forEach { date ->
        LogCollector.collectComponentLog(
                dataCenter = dataCenter,
                component = Component.RGS_PLATFORM,
                date = date,
                targetBaseFolderPath = targetBaseFolderPath
        ) {
        }
        LogCollector.collectComponentLog(
                dataCenter = dataCenter,
                component = Component.GSR,
                date = date,
                stack = AppStack.NONE,
                targetBaseFolderPath = targetBaseFolderPath
        ) {
        }
        LogCollector.collectComponentLog(
                dataCenter = dataCenter,
                component = Component.PAS,
                date = date,
                stack = AppStack.NONE,
                targetBaseFolderPath = targetBaseFolderPath
        ) {
        }
    }
}