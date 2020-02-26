package com.tapsell.task3.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.tapsell.task3.entities.AdvertiseEvent
import com.tapsell.task3.models.ClickEvent
import com.tapsell.task3.models.InvalidClickEventConsumerBuilder
import com.tapsell.task3.repositories.AdvertiseEventRepository
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

    enum class Margin(val string: String) {
        CLICK_EVENT_MARGIN("CLICK_EVENT_MARGIN")
    }

    @Scheduled(fixedRate = 120000, initialDelay = 125000) // 2 minutes and 5 seconds delay and then do this task every 2 minutes
    fun readInvalidEvents() {
        println("read invalid function called")
        val records: ConsumerRecords<String, String>? = invalidEvConsumer.poll(1000)
        var endOfPeriod = false
        records?.forEach { record ->
            val clickEvJson: String = record.value()
            println("invalid key of $clickEvJson found")
            var clickEvent: ClickEvent = ClickEvent("", 0, 0, "", "")
            try {
                clickEvent = objectMapper.readValue(clickEvJson, ClickEvent::class.java)
            } catch (e: Exception) {
//                if (endOfPeriod) return
                endOfPeriod = true
            }
            if (!endOfPeriod) {
                val adEvent: Optional<AdvertiseEvent> = adEventRepo.findById(clickEvent.requestId)
                if (adEvent.isPresent) {
                    adEvent.get().clickTime = clickEvent.clickTime
                    adEventRepo.save(adEvent.get())
                }
            } else {
                requestService.kafkaTemplate.send("invalidClickEv", record.value())
            }
        }
    }


    @Scheduled(fixedRate = 120000) // creating period margin for invalid click consumer to seperate clicks of specific periods of 2 minutes
    fun sendMargin() {
        requestService.kafkaTemplate.send("invalidClickEv", Margin.CLICK_EVENT_MARGIN.string)
    }

}