package com.tapsell.task3.services

import com.tapsell.task3.models.ClickEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.time.Duration


@Service
class ClickEventService(val requestService: RequestService) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass.simpleName)


    fun pushClickEvent(clickEvent: ClickEvent) {
        val clickJson: String = requestService.objectMapper.writeValueAsString(clickEvent)

        logger.info("click event received : $clickEvent")

        requestService.kafkaTemplate.send("clickEv", clickJson)

        logger.info("click event ${clickEvent.requestId} sent to kafka queue")
    }

    @KafkaListener(groupId = "advertiseEvent", topics = ["clickEv"])
    fun popClickEvent(clickEvJson: String) {
        val clickEvent = requestService.objectMapper.readValue(clickEvJson, ClickEvent::class.java)
        val eventDay = Duration.ofMillis(clickEvent.impressionTime).toDays() + 0 // todo omit duplication
        requestService.updateDailyEventStat(eventDay, clickEvent.adID, clickEvent.appId, false)
    }
}