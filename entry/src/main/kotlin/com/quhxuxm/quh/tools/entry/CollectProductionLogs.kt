package com.quhxuxm.quh.tools.entry

import com.quhxuxm.quh.tools.log.collector.AppStack
import com.quhxuxm.quh.tools.log.collector.DataCenter
import com.quhxuxm.quh.tools.log.collector.LogCollector
import com.quhxuxm.quh.tools.log.collector.LogFileCategory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.util.*

private val logger = LoggerFactory.getLogger("MAIN")

private fun printDownloadFilePaths(paths: Set<Path>) {
    paths.forEach {
        logger.info("$it is ready to use")
    }
}

private fun downloadGIBLogs() {
    runBlocking {
        val dataCenter = DataCenter.GIB
        val targetBaseFolderPath = Path.of("D:\\production\\IIM-74866")
        val calendar = Calendar.getInstance();
        calendar.set(2020, Calendar.AUGUST, 10)
        val date1 = calendar.time
        val dateToDownload = listOf(date1)
        dateToDownload.forEach { date ->
            logger.info("Collecting logs for data: {}", date)
//        LogAnalyzer.analyze(
//                date = date,
//                dataCenter = dataCenter,
//                logDownloadPath = targetBaseFolderPath,
//                stack = AppStack.A
//        )
            launch {
                LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.GSR_TOMCAT_GSR_ACCESS_LOG,
                        stack = AppStack.NONE,
                        date = date,
                        logDownloadPath = targetBaseFolderPath
                )
            }
            launch {
                LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.GSR_TOMCAT_GSR_LOG,
                        stack = AppStack.NONE,
                        date = date,
                        logDownloadPath = targetBaseFolderPath
                )
            }
            launch {
                LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.GSR_TOMCAT_PERFORMANCE_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                )
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.GSR_APACHE_GSR_ACCESS_LOG,
                        date = date,
                        stack = AppStack.A,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.RGS_PLATFORM_TOMCAT_PLATFORM_ACCESS_LOG,
                        date = date,
                        stack = AppStack.A,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.RGS_PLATFORM_TOMCAT_PLATFORM_LOG,
                        date = date,
                        stack = AppStack.A,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.NSS_TOMCAT_NSS_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.NSS_TOMCAT_NSS_ACCESS_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.NSS_TOMCAT_HOSTED_WS_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.TS_TOMCAT_TS_ACCESS_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.TS_TOMCAT_TS_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.PAS_TOMCAT_PAS_ACCESS_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.PAS_TOMCAT_PAS_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
        }
    }
}

private fun downloadMATLogs() {
    val dataCenter = DataCenter.MTA
    val targetBaseFolderPath = Path.of("D:\\production\\CSABA")
    val calendar = Calendar.getInstance();
    calendar.set(2020, Calendar.AUGUST, 6)
    val date1 = calendar.time
    val dateToDownload = listOf(date1)
    runBlocking {
//        launch {
//            printDownloadFilePaths(LogCollector.collectLog(
//                    dataCenter = dataCenter,
//                    logFileCategory = LogFileCategory.GSR_APACHE_GSR_ACCESS_LOG,
//                    date = date2,
//                    stack = AppStack.A,
//                    logDownloadPath = targetBaseFolderPath
//            ))
//        }
//        launch {
//            printDownloadFilePaths(LogCollector.collectLog(
//                    dataCenter = dataCenter,
//                    logFileCategory = LogFileCategory.GSR_TOMCAT_GSR_ACCESS_LOG,
//                    stack = AppStack.NONE,
//                    date = date1,
//                    logDownloadPath = targetBaseFolderPath
//            ))
//        }
        dateToDownload.forEach { date ->
            logger.info("Collecting logs for data: {}", date)
//        LogAnalyzer.analyze(
//                date = date,
//                dataCenter = dataCenter,
//                logDownloadPath = targetBaseFolderPath,
//                stack = AppStack.A
//        )
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.GSR_TOMCAT_GSR_ACCESS_LOG,
                        stack = AppStack.NONE,
                        date = date,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.GSR_TOMCAT_GSR_LOG,
                        stack = AppStack.NONE,
                        date = date,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.GSR_TOMCAT_PERFORMANCE_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.GSR_APACHE_GSR_ACCESS_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.RGS_PLATFORM_TOMCAT_PLATFORM_ACCESS_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.RGS_PLATFORM_TOMCAT_PLATFORM_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.NSS_TOMCAT_NSS_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.NSS_TOMCAT_NSS_ACCESS_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.NSS_TOMCAT_HOSTED_WS_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.TS_TOMCAT_TS_ACCESS_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.TS_TOMCAT_TS_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.PAS_TOMCAT_PAS_ACCESS_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
            launch {
                printDownloadFilePaths(LogCollector.collectLog(
                        dataCenter = dataCenter,
                        logFileCategory = LogFileCategory.PAS_TOMCAT_PAS_LOG,
                        date = date,
                        stack = AppStack.NONE,
                        logDownloadPath = targetBaseFolderPath
                ))
            }
        }
    }
}


fun main() {
    downloadMATLogs()
}
