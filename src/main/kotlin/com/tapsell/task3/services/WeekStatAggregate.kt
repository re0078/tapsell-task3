package com.tapsell.task3.services

import com.datastax.driver.core.querybuilder.QueryBuilder
import com.tapsell.task3.entities.DailyAdvertiseStatistics
import com.tapsell.task3.entities.WeekAdvertiseStatistics
import com.tapsell.task3.repositories.WeekAdvertiseStatisticsRepository
import org.slf4j.LoggerFactory
import org.springframework.data.cassandra.core.CassandraTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

@Service
class WeekStatAggregate(val cassandraTemplate: CassandraTemplate,
                        val weekStatRepo: WeekAdvertiseStatisticsRepository) {

    private val logger = LoggerFactory.getLogger(javaClass.simpleName)


    @Scheduled(fixedRate = 60000/*, cron = "000****"*/)
    fun aggregate() {
        val lastWeekDays = (1..6).map {
            Duration.ofMillis(Date().time).toDays() - it
        }

        val select = QueryBuilder.select().from("dailyStat")
                .where(QueryBuilder.`in`("day", lastWeekDays))


        cassandraTemplate.select(select, DailyAdvertiseStatistics::class.java)
                .groupBy {
                    print(it)
                    Pair(it.adId, it.appId)
                }.toList().forEach {
                    weekStatRepo.save(WeekAdvertiseStatistics(
                            adId = it.first.first,
                            appId = it.first.second,
                            impressionCount = it.second.sumBy { adStat -> adStat.impressionCount },
                            clickCount = it.second.sumBy { appStat -> appStat.clickCount }))
                }

        logger.info("Aggregate last week statistics \t ${Date().time} \t ${Duration.ofMillis(Date().time).toDays()}")
    }
}