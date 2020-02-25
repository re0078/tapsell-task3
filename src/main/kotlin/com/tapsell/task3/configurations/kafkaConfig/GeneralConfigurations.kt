package com.tapsell.task3.configurations.kafkaConfig

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GeneralConfigurations {
    @Bean
    fun createTopic1(): NewTopic {
        return NewTopic("clickEv", 1, 1.toShort())
    }

    @Bean
    fun createTopic2(): NewTopic {
        return NewTopic("impressionEv", 1, 1.toShort())
    }

    @Bean
    fun createInvalidClickEvTopic(): NewTopic {
        return NewTopic("invalidClickEv", 1, 1.toShort())
    }
}