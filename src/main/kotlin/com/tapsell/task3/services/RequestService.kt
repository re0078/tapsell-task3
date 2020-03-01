package com.tapsell.task3.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.tapsell.task3.models.ClickEvent
import com.tapsell.task3.models.ImpressionEvent
import com.tapsell.task3.repositories.DailyAdvertiseStatisticsRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

@Service
class RequestService(
        val kafkaTemplate: KafkaTemplate<String, String>,
        val objectMapper: ObjectMapper,
        val dailyAdEvRepo: DailyAdvertiseStatisticsRepository
) {

    val logger: Logger = LoggerFactory.getLogger(javaClass.simpleName)

    fun upsertDailyStatByImpressionEv(impressionEvent: ImpressionEvent) {
        val eventDay = Duration.ofMillis(impressionEvent.impressionTime).toDays()
        val dailyAdStatRecord = dailyAdEvRepo.findByDayAndAdIdAndAppId(eventDay, impressionEvent.adID, impressionEvent.appId)
        if (dailyAdStatRecord.isPresent) {
            dailyAdStatRecord.get().impressionCount += 1
            dailyAdEvRepo.insertRecord(dailyAdStatRecord.get().day, dailyAdStatRecord.get().adId, dailyAdStatRecord.get().appId, dailyAdStatRecord.get().impressionCount, dailyAdStatRecord.get().clickCount)
        } else {
            dailyAdEvRepo.insertRecord(eventDay, impressionEvent.adID, impressionEvent.appId, 1, 0)
        }
    }

    fun updateDailyStatByClickEv(clickEvent: ClickEvent) {
        val eventDay = Duration.ofMillis(Date(clickEvent.impressionTime).time).toDays()
        val dailyAdStatRecord = dailyAdEvRepo.findByDayAndAdIdAndAppId(eventDay, clickEvent.adId, clickEvent.appId)
        dailyAdStatRecord.get().clickCount += 1
        dailyAdEvRepo.save(dailyAdStatRecord.get())
    }
}
