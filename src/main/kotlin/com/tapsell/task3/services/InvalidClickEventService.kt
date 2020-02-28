package com.tapsell.task3.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.tapsell.task3.entities.AdvertiseEvent
import com.tapsell.task3.models.ClickEvent
import com.tapsell.task3.models.InvalidClickEventConsumerBuilder
import com.tapsell.task3.repositories.AdvertiseEventRepository
import com.tapsell.task3.configurations.kafkaConfig.GeneralConfigurations.TopicNames
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*

@Service
class InvalidClickEventService(private val invalidClickConsumerBuilder: InvalidClickEventConsumerBuilder,
                               val objectMapper: ObjectMapper,
                               val adEventRepo: AdvertiseEventRepository,
                               val requestService: RequestService) {

    private val invalidEvConsumer = invalidClickConsumerBuilder.build()
    // this should not be a bean because of the need to have multiple consumers with custom settings
    // the only thing is gonna change for that purpose is the names of the main and builder classes

    @Scheduled(fixedRate = 120000, initialDelay = 125000) // 2 minutes delay and then does this task every 2 minutes
    fun readInvalidEvents() {
        val records: ConsumerRecords<String, String>? = invalidEvConsumer.poll(1000)
        records?.forEach { record ->
            val clickEvent = objectMapper.readValue(record.value(), ClickEvent::class.java)
            if (adEventRepo.findById(clickEvent.requestId).isPresent)
                requestService.updateDailyStatByClickEv(clickEvent)
            else requestService.kafkaTemplate.send(TopicNames.INVALID_CLICK_EVENT, record.value())
        }
    }

}