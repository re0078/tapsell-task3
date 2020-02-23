package com.tapsell.task3.services

import com.tapsell.task3.models.ImpressionEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ImpressionEventService(val requestService: RequestService) { // todo an alternative method for inheritance and temporary solution


    fun pushImpressionEv(impressionEvent: ImpressionEvent) {
        val impressionEvJson: String = requestService.objectMapper.writeValueAsString(impressionEvent)

        requestService.logger.info("impression event ${impressionEvent.requestId} received")

        requestService.kafkaTemplate.send("impressionEv", impressionEvJson)

        requestService.logger.info("impressionEv $impressionEvent.requestId sent to kafka queue")
    }

    @KafkaListener(groupId = "advertiseEvent", topics = ["impressionEv"])
    fun popClickEvent(impressionJson: String) {
        val impressionEvent = requestService.objectMapper.readValue(impressionJson, ImpressionEvent::class.java)
        val eventDay = Duration.ofMillis(impressionEvent.impressionTime).toDays()

        requestService.updateDailyEventStat(eventDay, impressionEvent.adID, impressionEvent.appId, true)
    }

}