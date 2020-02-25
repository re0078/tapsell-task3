package com.tapsell.task3.models

import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.*


class InvalidClickEventConsumerBuilder {
    var properties: Properties = Properties()

    var topics: List<String> = arrayListOf()

    fun build(): KafkaConsumer<String, String> {
        val kafkaConsumer = KafkaConsumer<String, String>(properties)
        kafkaConsumer.subscribe(topics)
        return kafkaConsumer
    }

}