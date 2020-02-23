package com.tapsell.task2.configs

//import com.mongodb.MongoClientOptions
//import com.mongodb.MongoClientURI
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.core.CassandraTemplate
//import org.springframework.data.mongodb.MongoDbFactory
//import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.web.context.support.GenericWebApplicationContext
import java.util.*


@Configuration
@EnableKafka
class KafkaConfiguration(val kafkaProperties: KafkaProperties) {


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

    @Bean
    fun createTopic1(): NewTopic {
        return NewTopic("clickEv", 3, 1.toShort())
    }

    @Bean
    fun createTopic2(): NewTopic {
        return NewTopic("impressionEv", 3, 1.toShort())
    }


    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        return DefaultKafkaConsumerFactory<String, String>(
                kafkaProperties.buildConsumerProperties(), StringDeserializer(), StringDeserializer()
        )
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        return factory
    }

    @Bean
    fun stringConsumerFactory(): ConsumerFactory<String, String> {
        return DefaultKafkaConsumerFactory<String, String>(
                kafkaProperties.buildConsumerProperties(), StringDeserializer(), StringDeserializer()
        )
    }

    @Bean
    fun kafkaListenerStringContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String>? {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = stringConsumerFactory()
        return factory
    }

    @Bean
    fun byteArrayConsumerFactory(): ConsumerFactory<String?, ByteArray?> {
        return DefaultKafkaConsumerFactory(
                kafkaProperties.buildConsumerProperties(), StringDeserializer(), ByteArrayDeserializer()
        )
    }

    @Bean
    fun kafkaListenerByteArrayContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, ByteArray>? {
        val factory = ConcurrentKafkaListenerContainerFactory<String, ByteArray>()
        factory.consumerFactory = byteArrayConsumerFactory()
        return factory
    }

}