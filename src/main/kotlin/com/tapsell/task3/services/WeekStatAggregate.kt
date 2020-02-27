package com.tapsell.task3.services

import com.tapsell.task3.repositories.DailyAdvertiseStatisticsRepository
import com.tapsell.task3.repositories.WeekAdvertiseStatisticsRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

@Service
class WeekStatAggregate(val dailyAdStatRepo: DailyAdvertiseStatisticsRepository,
                        val weekStatRepo: WeekAdvertiseStatisticsRepository) {

    private val logger = LoggerFactory.getLogger(javaClass.simpleName)


    @Scheduled(cron = "0 0 0 * * *")
    fun aggregate() {
        val lastWeekDays = (1..6).map {
            Duration.ofMillis(Date().time).toDays() - it
        }

        dailyAdStatRepo.findByDayIn(lastWeekDays)
                .groupBy {
                    print(it)
                    Pair(it.adId, it.appId)
                }.toList().forEach {
                    weekStatRepo.insertRecord(
                            adId = it.first.first,
                            appId = it.first.second,
                            impressionCount = it.second.sumBy { adStat -> adStat.impressionCount },
                            clickCount = it.second.sumBy { appStat -> appStat.clickCount })
                }

        logger.info("Aggregate last week statistics \t ${Date().time} \t ${Duration.ofMillis(Date().time).toDays()}")
    }
}