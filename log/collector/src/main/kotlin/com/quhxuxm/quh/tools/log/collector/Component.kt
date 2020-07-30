package com.quhxuxm.quh.tools.log.collector

enum class Component(val id: String, val appLogFileName: String) {
    GSR("gsr", "gsr.log"),
    RGS_PLATFORM("rgs", "platform.log"),
    PAS("pas", "pas.log"),
    NSS("nss", "nss.log"),
    UID("uid", "uid.log"),
    TS("ts", "ts.log")
}