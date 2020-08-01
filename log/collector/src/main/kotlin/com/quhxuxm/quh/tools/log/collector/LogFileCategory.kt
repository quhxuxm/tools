package com.quhxuxm.quh.tools.log.collector

enum class LogFileCategory(val id: String, val serverName: String, val logFile: String) {
    GSR_TOMCAT_GSR_LOG("gsr", "gsr-app", "gsr.log"),
    GSR_TOMCAT_GSR_ACCESS_LOG("gsr", "gsr-app", "gsr_access.log"),
    GSR_TOMCAT_PERFORMANCE_LOG("gsr", "gsr-app", "performance.log"),
    GSR_APACHE_GSR_ACCESS_LOG("rgs", "rgs-web", "gsr_access.log"),
    RGS_PLATFORM_TOMCAT_PLATFORM_LOG("rgs", "rgs-app", "platform.log"),
    RGS_PLATFORM_TOMCAT_PLATFORM_ACCESS_LOG("rgs", "rgs-app", "platform_access.log"),
    PAS_TOMCAT_PAS_LOG("pas", "pas-app", "pas.log"),
    PAS_TOMCAT_PAS_ACCESS_LOG("pas", "pas-app", "pas_access.log"),
    NSS_TOMCAT_NSS_LOG("nss", "nss-app", "nss.log"),
    NSS_TOMCAT_HOSTED_WS_LOG("nss", "nss-app", "hosted-ws.log"),
    NSS_TOMCAT_NSS_ACCESS_LOG("nss", "nss-app", "nss_access.log"),
    UID_TOMCAT_UID_LOG("uid", "uid-app", "uid.log"),
    UID_TOMCAT_UID_ACCESS_LOG("uid", "uid-app", "uid_access.log"),
    TS_TOMCAT_TS_LOG("ts", "ts-app", "ts.log"),
    TS_TOMCAT_TS_ACCESS_LOG("ts", "ts-app", "ts_access.log")
}