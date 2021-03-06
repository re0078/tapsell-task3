package com.tapsell.task3.services

import com.tapsell.task3.configurations.kafkaConfig.GeneralConfigurations.TopicNames
import com.tapsell.task3.configurations.kafkaConfig.ConsumerConfiguration.Companion.consumerGroupId
import com.tapsell.task3.entities.AdvertiseEvent
import com.tapsell.task3.models.ImpressionEvent
import com.tapsell.task3.repositories.AdvertiseEventRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ImpressionEventService(val requestService: RequestService,
                             val adEvRepo: AdvertiseEventRepository) { // todo an alternative method for inheritance and temporary solution


    fun pushImpressionEv(impressionEvent: ImpressionEvent) {
        val impressionEvJson: String = requestService.objectMapper.writeValueAsString(impressionEvent)

        requestService.logger.info("impression event ${impressionEvent.requestId} received")

        requestService.kafkaTemplate.send(TopicNames.IMPRESSION_EVENT, impressionEvJson)

        requestService.logger.info("impressionEv $impressionEvent.requestId sent to kafka queue")
    }

    @KafkaListener(groupId = consumerGroupId, topics = [TopicNames.IMPRESSION_EVENT])
    fun popImpressionEvent(impressionJson: String) {
        val impressionEvent = requestService.objectMapper.readValue(impressionJson, ImpressionEvent::class.java)
        requestService.logger.info("in the popImpressionEvent and json is $impressionJson")
        adEvRepo.save(AdvertiseEvent(impressionEvent.requestId,
                impressionEvent.adID,
                impressionEvent.adTitle,
                impressionEvent.advertiserCost,
                impressionEvent.appId,
                impressionEvent.appTitle,
                impressionEvent.impressionTime,
                null))
        requestService.upsertDailyStatByImpressionEv(impressionEvent)
    }
}
