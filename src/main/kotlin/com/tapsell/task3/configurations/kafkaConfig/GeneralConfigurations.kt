package com.tapsell.task3.configurations.kafkaConfig

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GeneralConfigurations {

    object TopicNames {
        const val CLICK_EVENT = "clickEv"
        const val IMPRESSION_EVENT = "impressionEv"
        const val INVALID_CLICK_EVENT = "invalidClickEv"
    }


    @Bean
    fun createTopic1(): NewTopic {
        return NewTopic(TopicNames.CLICK_EVENT, 1, 1.toShort())
    }

    @Bean
    fun createTopic2(): NewTopic {
        return NewTopic(TopicNames.IMPRESSION_EVENT, 1, 1.toShort())
    }

    @Bean
    fun createInvalidClickEvTopic(): NewTopic {
        return NewTopic(TopicNames.INVALID_CLICK_EVENT, 1, 1.toShort())
    }
}