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

    init {
        val tmpFolderPath = Path.of(TMP_FOLDER)
        if (!tmpFolderPath.toFile().exists()) {
            tmpFolderPath.toFile().mkdir()
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

    fun collectComponentLog(dataCenter: String, component: String, dataCenterShortName: String, appIndex: Int,
                            stack: String = "a", logFileName: String, date: Date, targetBaseFolderPath: String,
                            callback: (resultFilePath: Path) -> Unit) {
        val dataFormat = SimpleDateFormat("yyyy-MM-dd")
        val dataSuffix = dataFormat.format(date)
        val componentPath = Path.of(targetBaseFolderPath, component)
        if (!componentPath.toFile().exists()) {
            componentPath.toFile().mkdir()
        }
        this.collect(
                "http://sf-prod-arch01.corp.wagerworks.com/archives/$dataCenter/$component/${dataCenterShortName}prod/" +
                        "${dataCenterShortName}-$component-app0${appIndex}${stack}/${logFileName}.${dataSuffix}.gz",
                "${targetBaseFolderPath}\\${component}\\${logFileName}.${dataSuffix}.app0${appIndex}.log", callback)
    }
}

fun main() {
    val calendar = Calendar.getInstance();
    calendar.set(2020, Calendar.JULY, 27)
    val date = calendar.time
    val targetBaseFolderPath = "D:\\logs\\IIM-73824"
    val dataCenter = "GIB"
    val dataCenterShortName = "gi"
    (1..4).forEach {
        val component = "rgs"
        val logFileName = "platform.log"
        LogCollector.collectComponentLog(dataCenter = dataCenter, component = component,
                dataCenterShortName = dataCenterShortName, appIndex = it, logFileName = logFileName, date = date,
                targetBaseFolderPath = targetBaseFolderPath) {
        }
    }
    (1..4).forEach {
        val component = "gsr"
        val logFileName = "gsr.log"
        LogCollector.collectComponentLog(dataCenter = dataCenter, component = component,
                dataCenterShortName = dataCenterShortName, appIndex = it, logFileName = logFileName, date = date,
                stack = "",
                targetBaseFolderPath = targetBaseFolderPath) {
        }
    }
    (1..4).forEach {
        val component = "pas"
        val logFileName = "pas.log"
        LogCollector.collectComponentLog(dataCenter = dataCenter, component = component,
                dataCenterShortName = dataCenterShortName, appIndex = it, logFileName = logFileName, date = date,
                stack = "",
                targetBaseFolderPath = targetBaseFolderPath) {
        }
    }
}