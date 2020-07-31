package com.quhxuxm.quh.tools.entry

import com.quhxuxm.quh.tools.log.collector.AppStack
import com.quhxuxm.quh.tools.log.collector.Component
import com.quhxuxm.quh.tools.log.collector.DataCenter
import com.quhxuxm.quh.tools.log.collector.LogCollector
import kotlinx.coroutines.runBlocking
import java.util.*

fun main() {
    val dataCenter = DataCenter.GIB
    val targetBaseFolderPath = "D:\\logs\\IIM-73824"
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
    val dateToDownload = listOf(date1, date2)
    runBlocking {
        dateToDownload.forEach { date ->
            LogCollector.collectComponentLog(
                    dataCenter = dataCenter,
                    component = Component.RGS_PLATFORM_TOMCAT,
                    date = date,
                    targetBaseFolderPath = targetBaseFolderPath
            )
            LogCollector.collectComponentLog(
                    dataCenter = dataCenter,
                    component = Component.GSR,
                    date = date,
                    stack = AppStack.NONE,
                    targetBaseFolderPath = targetBaseFolderPath
            )
            LogCollector.collectComponentLog(
                    dataCenter = dataCenter,
                    component = Component.PAS,
                    date = date,
                    stack = AppStack.NONE,
                    targetBaseFolderPath = targetBaseFolderPath
            )
            LogCollector.collectComponentLog(
                    dataCenter = dataCenter,
                    component = Component.TS,
                    date = date,
                    stack = AppStack.NONE,
                    targetBaseFolderPath = targetBaseFolderPath
            )
        }
    }
}