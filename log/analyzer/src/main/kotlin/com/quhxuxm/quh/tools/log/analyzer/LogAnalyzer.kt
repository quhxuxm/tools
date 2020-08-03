package com.quhxuxm.quh.tools.log.analyzer

import com.quhxuxm.quh.tools.log.collector.AppStack
import com.quhxuxm.quh.tools.log.collector.DataCenter
import com.quhxuxm.quh.tools.log.collector.LogCollector
import com.quhxuxm.quh.tools.log.collector.LogFileCategory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.collections.HashMap

class LogItem {
}

object LogAnalyzer {
    private val logger = LoggerFactory.getLogger(LogAnalyzer::class.java)

    private fun parseLog4JLine(logLine: String, previousLogItem: LogItem): LogItem {
        val logLineParts = logLine.split(" ")
    }

    suspend fun analyze(date: Date, dataCenter: DataCenter, stack: AppStack, logDownloadPath: Path) {
        val readyLogs = HashMap<LogFileCategory, Set<Path>>()
        coroutineScope {
            launch {
                val gsrApacheAccessLogs = LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.GSR_APACHE_GSR_ACCESS_LOG,
                        date = date,
                        stack = stack,
                        logDownloadPath = logDownloadPath,
                        indexRange = 1..3
                )
                readyLogs.put(LogFileCategory.GSR_APACHE_GSR_ACCESS_LOG, gsrApacheAccessLogs)
            }
            launch {
                val gsrTomcatAccessLogs = LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.GSR_TOMCAT_GSR_ACCESS_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = logDownloadPath
                )
                readyLogs.put(LogFileCategory.GSR_TOMCAT_GSR_ACCESS_LOG, gsrTomcatAccessLogs)
            }
            launch {
                val gsrTomcatPerformanceLogs = LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.GSR_TOMCAT_PERFORMANCE_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = logDownloadPath
                )
                readyLogs.put(LogFileCategory.GSR_TOMCAT_PERFORMANCE_LOG, gsrTomcatPerformanceLogs)
            }
            launch {
                val rgsPlatformTomcatAccessLogs = LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.RGS_PLATFORM_TOMCAT_PLATFORM_ACCESS_LOG,
                        date = date,
                        stack = stack,
                        logDownloadPath = logDownloadPath
                )
                readyLogs.put(LogFileCategory.RGS_PLATFORM_TOMCAT_PLATFORM_ACCESS_LOG, rgsPlatformTomcatAccessLogs)
            }
            launch {
                val rgsPlatformTomcatPlatformLogs = LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.RGS_PLATFORM_TOMCAT_PLATFORM_LOG,
                        date = date,
                        stack = stack,
                        logDownloadPath = logDownloadPath
                )
                readyLogs.put(LogFileCategory.RGS_PLATFORM_TOMCAT_PLATFORM_LOG, rgsPlatformTomcatPlatformLogs)
            }
        }

        readyLogs.forEach { logCategory, logPaths ->
            logPaths.forEach {
                when (logCategory) {
                    LogFileCategory.GSR_TOMCAT_GSR_ACCESS_LOG -> {
                        logger.info("Begin to analyze: $it")
                        Files.readAllLines(it).asSequence().forEach { logLine ->
                            println(logLine)
                        }
                    }
                    LogFileCategory.GSR_APACHE_GSR_ACCESS_LOG -> {
                        logger.info("Begin to analyze: $it")
                        Files.readAllLines(it).asSequence().forEach { logLine ->
                            println(logLine)
                        }
                    }
                    LogFileCategory.GSR_TOMCAT_PERFORMANCE_LOG -> {
                        logger.info("Begin to analyze: $it")
                        Files.readAllLines(it).asSequence().forEach { logLine ->
                            println(logLine)
                        }
                    }
                    LogFileCategory.GSR_TOMCAT_GSR_LOG -> {
                        logger.info("Begin to analyze: $it")
                        Files.readAllLines(it).asSequence().forEach { logLine ->
                            println(logLine)
                        }
                    }
                }
            }
        }
    }
}