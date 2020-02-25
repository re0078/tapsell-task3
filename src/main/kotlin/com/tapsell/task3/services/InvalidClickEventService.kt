package com.tapsell.task3.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.tapsell.task3.entities.AdvertiseEvent
import com.tapsell.task3.models.ClickEvent
import com.tapsell.task3.models.InvalidClickEventConsumerBuilder
import com.tapsell.task3.repositories.AdvertiseEventRepository
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*

@Service
class InvalidClickEventService(private val invalidClickConsumerBuilder: InvalidClickEventConsumerBuilder,
                               val objectMapper: ObjectMapper,
                               val adEventRepo: AdvertiseEventRepository) {

    private val invalidEvConsumer = invalidClickConsumerBuilder.build()

    @Scheduled(fixedRate = 120000)
    fun readInvalidEvents() {
        val records: ConsumerRecords<String, String>? = invalidEvConsumer.poll(1000)
        // just one record is polled because of one single topic

        records?.forEach { record ->
            val clickEvJson = record.value()
            val clickEvent = objectMapper.readValue(clickEvJson, ClickEvent::class.java)
            val adEvent: Optional<AdvertiseEvent> = adEventRepo.findById(clickEvent.requestId)
            if (adEvent.isPresent) {
                adEvent.get().clickTime = clickEvent.clickTime
                adEventRepo.save(adEvent.get())
            }
        }
    }

}