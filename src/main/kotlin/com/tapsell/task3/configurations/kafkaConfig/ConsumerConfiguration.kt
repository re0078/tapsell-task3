package com.tapsell.task3.configurations.kafkaConfig

import com.tapsell.task3.models.InvalidClickEventConsumerBuilder
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import java.util.*

@Configuration
class ConsumerConfiguration(val kafkaProperties: KafkaProperties) {

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

    @Bean
    fun invalidClickEvConsumer(): InvalidClickEventConsumerBuilder {
        val consumerBuilder = InvalidClickEventConsumerBuilder()
        val properties = Properties()
        properties.setProperty("bootstrap.servers", "localhost:9092")
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("group.id", consumerGroupId);
        consumerBuilder.properties = properties
        val topics = arrayListOf(GeneralConfigurations.TopicNames.INVALID_CLICK_EVENT)
        consumerBuilder.topics = topics
        return consumerBuilder
    }

    companion object {
        const val consumerGroupId: String = "advertiseEvent"
    }
}