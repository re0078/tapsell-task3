package com.tapsell.task3.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.tapsell.task3.repositories.DailyAdvertiseStatisticsRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class RequestService(
        val kafkaTemplate: KafkaTemplate<String, String>,
        val objectMapper: ObjectMapper,
        val dailyAdEvRepo: DailyAdvertiseStatisticsRepository
) {

    val logger: Logger = LoggerFactory.getLogger(javaClass.simpleName)


    fun updateDailyEventStat(day: Long, adId: String, appId: String, newImpression: Boolean) {
        val dailyAdStatRecord = dailyAdEvRepo.findByDayAndAdIdAndAppId(day, adId, appId)

        if (dailyAdStatRecord.isPresent) {
            if (newImpression) dailyAdStatRecord.get().impressionCount += 1 else dailyAdStatRecord.get().clickCount += 1
            dailyAdEvRepo.insertRecord(dailyAdStatRecord.get().day, dailyAdStatRecord.get().adId, dailyAdStatRecord.get().appId, dailyAdStatRecord.get().impressionCount, dailyAdStatRecord.get().clickCount)
        } else {

            dailyAdEvRepo.insertRecord(day, adId, appId, if (newImpression) 1 else 0, if (newImpression) 0 else 1)
        }
    }
}
