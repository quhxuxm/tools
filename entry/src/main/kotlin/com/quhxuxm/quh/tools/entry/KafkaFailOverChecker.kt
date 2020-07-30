package com.quhxuxm.quh.tools.entry

import com.quhxuxm.quh.tools.kafka.checker.KafkaData
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun main() {
    val kafkaProducerProperties = Properties()
    kafkaProducerProperties.put("bootstrap.servers", "localhost:9081,localhost:9082,localhost:9083")
    kafkaProducerProperties.put("acks", "1")
    kafkaProducerProperties.put("retries", "0")
    kafkaProducerProperties.put("batch.size", "16384")
    kafkaProducerProperties.put("compression.type", "snappy")
    kafkaProducerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    kafkaProducerProperties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
    kafkaProducerProperties.put("request.timeout.ms", "4000")
    kafkaProducerProperties.put("delivery.timeout.ms", "6000")
    kafkaProducerProperties.put("max.block.ms", "5000")
    val kafkaProducer = KafkaProducer<String, ByteArray>(kafkaProducerProperties)
    val threadPool = Executors.newFixedThreadPool(10)
    while (true) {
        (1..100).forEach {
            threadPool.submit {
                val recordKey = (Math.abs(Random.nextInt()) % 8).toString()
                val recordData = KafkaData(UUID.randomUUID().toString())
                val producerRecord = ProducerRecord<String, ByteArray>("test-topic", recordKey, recordData.data)
                println("Begin: send kafka record, recordId=${recordData.id}, partitionKey=$recordKey")
                val result: Future<RecordMetadata>
                try {
                    result = kafkaProducer.send(producerRecord)
                    println("After: send kafka record, recordId=${recordData.id}, partitionKey=$recordKey")
                } catch (e: Exception) {
                    println("Fail: send kafka record get exception, recordId=${recordData.id}, partitionKey=$recordKey")
                    e.printStackTrace()
                    return@submit
                }
                try {
                    val recordMetadata = result.get(6000, TimeUnit.MILLISECONDS)
                    println("Get kafka record send result, recordId=${recordData.id}, partitionKey=$recordKey")
                    println("Record meta data partitionKey = ${recordMetadata.partition()}")
                    println("Record meta data timestamp = ${recordMetadata.timestamp()}")
                    println("Record meta data offset = ${recordMetadata.offset()}")
                } catch (e: Exception) {
                    println("Fail to get meta data for recordId=${recordData.id}")
                    e.printStackTrace()
                }
            }
        }
        Thread.sleep(100)
    }
}