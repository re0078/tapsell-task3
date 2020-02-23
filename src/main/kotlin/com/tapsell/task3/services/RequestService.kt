package com.tapsell.task3.services

import com.datastax.driver.core.querybuilder.QueryBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.tapsell.task3.entities.DailyAdvertiseStatistics
import com.tapsell.task3.repositories.DailyAdvertiseStatisticsRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.cassandra.core.CassandraTemplate
import org.springframework.data.cassandra.core.InsertOptions
import org.springframework.data.cassandra.core.insert
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class RequestService(
        val kafkaTemplate: KafkaTemplate<String, String>,
        val objectMapper: ObjectMapper,
        val cassandraTemplate: CassandraTemplate,
        val dailyAdEvRepo: DailyAdvertiseStatisticsRepository
) {

    val logger: Logger = LoggerFactory.getLogger(javaClass.simpleName)


    fun updateDailyEventStat(day: Long, adId: String, appId: String, newImpression: Boolean) {
        val select = QueryBuilder.select().from("dailyAdEventStat")                 // todo make sure this exists
                .where(QueryBuilder.eq(arrayListOf("adId, apId"), arrayListOf(adId, appId)))
                .and(QueryBuilder.eq("day", day))


        val dailyAdStatList = cassandraTemplate.select(select, DailyAdvertiseStatistics::class.java)
        if (dailyAdStatList.size > 0) {
            val dailyAdStat = dailyAdStatList[0]
            if (newImpression) dailyAdStat.impressionCount += 1 else dailyAdStat.clickCount += 1
            dailyAdEvRepo.save(dailyAdStat)
        } else {


            val insert = QueryBuilder.insertInto("dailyAdEventStat")
                    .values(arrayListOf("day", "adId", "appId", "impressionCount", "clickCount"),
                            arrayListOf(day, adId, appId, if (newImpression) 1 else 0, if (newImpression) 0 else 1))
                    .using(QueryBuilder.ttl(50))

            cassandraTemplate.insert(DailyAdvertiseStatistics(day, adId, appId, if (newImpression) 1 else 0, if (newImpression) 0 else 1))

        }
    }
}