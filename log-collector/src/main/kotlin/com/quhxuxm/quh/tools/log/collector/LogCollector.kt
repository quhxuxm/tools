package com.quhxuxm.quh.tools.log.collector

import java.io.FileInputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.GZIPInputStream

object LogCollector {
    private const val TMP_FOLDER = "./tmp"
    private const val LOG_SERVER_BASE_URL = "http://sf-prod-arch01.corp.wagerworks.com/archives"

    init {
        val tmpFolderPath = Path.of(TMP_FOLDER)
        if (!tmpFolderPath.toFile().exists()) {
            tmpFolderPath.toFile().mkdirs()
        }
    }

    private fun unzip(zipFilePath: Path, outputFilePath: Path) {
        val gzipFileInputStream = GZIPInputStream(FileInputStream(zipFilePath.toFile()))
        Files.copy(gzipFileInputStream, outputFilePath, StandardCopyOption.REPLACE_EXISTING)
    }

    fun collect(logPath: String, targetPath: String, callback: (resultFilePath: Path) -> Unit) {
        try {
            println("Downloading file: ${logPath}")
            val remoteLogUrl = URL(logPath)
            val randomFileName = UUID.randomUUID().toString().replace("-", "")
            val downloadPath = Path.of(TMP_FOLDER, randomFileName)
            Files.copy(remoteLogUrl.openStream(), downloadPath, StandardCopyOption.REPLACE_EXISTING)
            val resultFilePath = Path.of(targetPath)
            println("Unzip to file: ${targetPath}")
            this.unzip(downloadPath, resultFilePath)
            callback(resultFilePath)
        } catch (e: Exception) {
            println("Fail to collect file: ${logPath} because of exception.")
            e.printStackTrace()
        }
    }

    fun collectComponentLog(dataCenter: DataCenter, component: String, appIndex: Int,
                            stack: AppStack = AppStack.A, logFileName: String, date: Date, targetBaseFolderPath: String,
                            callback: (resultFilePath: Path) -> Unit) {
        val dataFormat = SimpleDateFormat("yyyy-MM-dd")
        val dataSuffix = dataFormat.format(date)
        val targetFolderPath = Path.of(targetBaseFolderPath, dataCenter.id, component)
        if (!targetFolderPath.toFile().exists()) {
            targetFolderPath.toFile().mkdirs()
        }
        this.collect(
                "$LOG_SERVER_BASE_URL/${dataCenter.id}/$component/${dataCenter.shortName}prod/" +
                        "${dataCenter.shortName}-$component-app0${appIndex}${stack.id}/${logFileName}.${dataSuffix}.gz",
                Path.of(targetFolderPath.toString(), "${logFileName}.${dataSuffix}.app0${appIndex}.log").toString(),
                callback)
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
        (1..4).forEach {
            val component = "rgs"
            val logFileName = "platform.log"
            LogCollector.collectComponentLog(dataCenter = dataCenter, component = component,
                    appIndex = it, logFileName = logFileName, date = date,
                    targetBaseFolderPath = targetBaseFolderPath) {
            }
        }
        (1..4).forEach {
            val component = "gsr"
            val logFileName = "gsr.log"
            LogCollector.collectComponentLog(dataCenter = dataCenter, component = component,
                    appIndex = it, logFileName = logFileName, date = date,
                    stack = AppStack.NONE,
                    targetBaseFolderPath = targetBaseFolderPath) {
            }
        }
        (1..4).forEach {
            val component = "pas"
            val logFileName = "pas.log"
            LogCollector.collectComponentLog(dataCenter = dataCenter, component = component,
                    appIndex = it, logFileName = logFileName, date = date,
                    stack = AppStack.NONE,
                    targetBaseFolderPath = targetBaseFolderPath) {
            }
        }
    }
}