package com.quhxuxm.quh.tools.log.collector

enum class Component(val id: String, val serverName: String, val logFiles: Array<String>) {
    GSR("gsr", "gsr-app", arrayOf("gsr.log", "gsr_access.log")),
    RGS_PLATFORM_TOMCAT("rgs", "rgs-app", arrayOf("platform.log", "platform_access.log")),
    RGS_PLATFORM_APACHE("rgs", "rgs-web", arrayOf("platform_access.log")),
    PAS("pas", "pas-app", arrayOf("pas.log", "pas_access.log")),
    NSS("nss", "nss-app", arrayOf("nss.log", "nss_access.log")),
    UID("uid", "uid-app", arrayOf("uid.log", "uid_access.log")),
    TS("ts", "ts-app", arrayOf("ts.log", "ts_access.log"))
}