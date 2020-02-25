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
        val dailyAdStatList = dailyAdEvRepo.findByDayAndAdIdAndAppId(day, adId, appId)

        if (dailyAdStatList.isNotEmpty()) {
            val dailyAdStat = dailyAdStatList[0] // there is only one element in the list
            if (newImpression) dailyAdStat.impressionCount += 1 else dailyAdStat.clickCount += 1
            dailyAdEvRepo.insertRecord(dailyAdStat.day, dailyAdStat.adId, dailyAdStat.appId, dailyAdStat.impressionCount, dailyAdStat.clickCount)
        } else {

            dailyAdEvRepo.insertRecord(day, adId, appId, if (newImpression) 1 else 0, if (newImpression) 0 else 1)
        }
    }
}
