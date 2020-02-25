package com.tapsell.task3.configurations.kafkaConfig

//import com.mongodb.MongoClientOptions
//import com.mongodb.MongoClientURI
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
//import org.springframework.data.mongodb.MongoDbFactory
//import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.*
import java.util.*


@Configuration
@EnableKafka
class ProducerConfiguration(val kafkaProperties: KafkaProperties) {

    @Bean
    fun producerConfigs(): Map<String, Any> {
        val props: MutableMap<String, Any> = HashMap(kafkaProperties.buildProducerProperties())
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        return props
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        return DefaultKafkaProducerFactory(producerConfigs())
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }
}