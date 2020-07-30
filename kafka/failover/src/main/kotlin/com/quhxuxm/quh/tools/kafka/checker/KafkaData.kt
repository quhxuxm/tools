package com.quhxuxm.quh.tools.kafka.checker

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.random.Random

data class KafkaData(val id: String) {
    val data: ByteArray

    init {
        var tmpData = ""
        for (i in 1..100) {
            tmpData += UUID.randomUUID().toString()
        }
        this.data = tmpData.toByteArray()
    }
}
