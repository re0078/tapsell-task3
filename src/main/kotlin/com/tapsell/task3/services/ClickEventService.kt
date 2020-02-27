package com.tapsell.task3.services

import com.tapsell.task3.entities.AdvertiseEvent
import com.tapsell.task3.models.ClickEvent
import com.tapsell.task3.repositories.AdvertiseEventRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*
import com.tapsell.task3.configurations.kafkaConfig.GeneralConfigurations.TopicNames
import com.tapsell.task3.configurations.kafkaConfig.ConsumerConfiguration.Companion.consumerGroupId

@Service
class ClickEventService(val requestService: RequestService,
                        val adEvRepo: AdvertiseEventRepository) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass.simpleName)


    fun pushClickEvent(clickEvent: ClickEvent) {
        val clickJson: String = requestService.objectMapper.writeValueAsString(clickEvent)

        logger.info("click event received : $clickEvent")

        requestService.kafkaTemplate.send(TopicNames.CLICK_EVENT, clickJson)

        logger.info("click event ${clickEvent.requestId} sent to kafka queue")
    }

    @KafkaListener(groupId = consumerGroupId, topics = [TopicNames.CLICK_EVENT])
    fun popClickEvent(clickEvJson: String) {
        val clickEvent = requestService.objectMapper.readValue(clickEvJson, ClickEvent::class.java)
        val eventDay = Duration.ofMillis(clickEvent.impressionTime).toDays() + 0 // todo omit duplication
        val adEvent: Optional<AdvertiseEvent> = adEvRepo.findById(clickEvent.requestId)
        if (adEvent.isPresent) {
            val newAdEv = adEvent.get()
            newAdEv.clickTime = clickEvent.clickTime
            adEvRepo.save(newAdEv)
            requestService.updateDailyEventStat(eventDay, clickEvent.adId, clickEvent.appId, false)
        } else {
            requestService.kafkaTemplate.send(TopicNames.INVALID_CLICK_EVENT, clickEvJson)
        }
    }
}