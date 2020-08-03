package com.quhxuxm.quh.tools.entry

import com.quhxuxm.quh.tools.log.analyzer.LogAnalyzer
import com.quhxuxm.quh.tools.log.collector.AppStack
import com.quhxuxm.quh.tools.log.collector.DataCenter
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

fun main() = runBlocking {
    val dataCenter = DataCenter.GIB
    val targetBaseFolderPath = Path.of("D:\\logs\\IIM-73824")
    val calendar = Calendar.getInstance();
    calendar.set(2020, Calendar.JULY, 26)
    val date1 = calendar.time
    calendar.set(2020, Calendar.JULY, 27)
    val date2 = calendar.time
//    calendar.set(2020, Calendar.JULY, 25)
//    val date3 = calendar.time
//    calendar.set(2020, Calendar.JULY, 24)
//    val date4 = calendar.time
//    calendar.set(2020, Calendar.JULY, 23)
//    val date5 = calendar.time
    val dateToDownload = listOf(date1)
    dateToDownload.forEach { date ->
        logger.info("Collecting logs for data: {}", date)

        LogAnalyzer.analyze(
                date = date,
                dataCenter = dataCenter,
                logDownloadPath = targetBaseFolderPath,
                stack = AppStack.A
        )
//        launch {
//            LogCollector.collectLog(
//                    dataCenter = dataCenter,
//                    logFileCategory = LogFileCategory.GSR_TOMCAT_GSR_ACCESS_LOG,
//                    stack = AppStack.NONE,
//                    date = date,
//                    logDownloadPath = targetBaseFolderPath
//            )
//        }
//        launch {
//            LogCollector.collectLog(
//                    dataCenter = dataCenter,
//                    logFileCategory = LogFileCategory.GSR_TOMCAT_GSR_LOG,
//                    stack = AppStack.NONE,
//                    date = date,
//                    logDownloadPath = targetBaseFolderPath
//            )
//        }
//        launch {
//            LogCollector.collectLog(
//                    dataCenter = dataCenter,
//                    logFileCategory = LogFileCategory.GSR_TOMCAT_PERFORMANCE_LOG,
//                    date = date,
//                    stack = AppStack.NONE,
//                    logDownloadPath = targetBaseFolderPath
//            )
//        }
//        launch {
//            printDownloadFilePaths(LogCollector.collectLog(
//                    dataCenter = dataCenter,
//                    logFileCategory = LogFileCategory.GSR_APACHE_GSR_ACCESS_LOG,
//                    date = date,
//                    stack = AppStack.A,
//                    logDownloadPath = targetBaseFolderPath
//            ))
//        }
//        launch {
//            printDownloadFilePaths(LogCollector.collectLog(
//                    dataCenter = dataCenter,
//                    logFileCategory = LogFileCategory.RGS_PLATFORM_TOMCAT_PLATFORM_ACCESS_LOG,
//                    date = date,
//                    stack = AppStack.A,
//                    logDownloadPath = targetBaseFolderPath
//            ))
//        }
//        launch {
//            printDownloadFilePaths(LogCollector.collectLog(
//                    dataCenter = dataCenter,
//                    logFileCategory = LogFileCategory.RGS_PLATFORM_TOMCAT_PLATFORM_LOG,
//                    date = date,
//                    stack = AppStack.A,
//                    logDownloadPath = targetBaseFolderPath
//            ))
//        }
//        launch {
//            printDownloadFilePaths(LogCollector.collectLog(
//                    dataCenter = dataCenter,
//                    logFileCategory = LogFileCategory.NSS_TOMCAT_NSS_LOG,
//                    date = date,
//                    stack = AppStack.NONE,
//                    logDownloadPath = targetBaseFolderPath
//            ))
//        }
//        launch {
//            printDownloadFilePaths(LogCollector.collectLog(
//                    dataCenter = dataCenter,
//                    logFileCategory = LogFileCategory.NSS_TOMCAT_NSS_ACCESS_LOG,
//                    date = date,
//                    stack = AppStack.NONE,
//                    logDownloadPath = targetBaseFolderPath
//            ))
//        }
//        launch {
//            printDownloadFilePaths(LogCollector.collectLog(
//                    dataCenter = dataCenter,
//                    logFileCategory = LogFileCategory.NSS_TOMCAT_HOSTED_WS_LOG,
//                    date = date,
//                    stack = AppStack.NONE,
//                    logDownloadPath = targetBaseFolderPath
//            ))
//        }
    }
}