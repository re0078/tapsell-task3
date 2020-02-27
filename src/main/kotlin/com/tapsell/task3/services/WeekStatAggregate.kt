package com.tapsell.task3.services

import com.tapsell.task3.repositories.DailyAdvertiseStatisticsRepository
import com.tapsell.task3.repositories.WeekAdvertiseStatisticsRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*
import javax.annotation.PostConstruct

@Service
class WeekStatAggregate(val dailyAdStatRepo: DailyAdvertiseStatisticsRepository,
                        val weekStatRepo: WeekAdvertiseStatisticsRepository) {

    private val logger = LoggerFactory.getLogger(javaClass.simpleName)


    @Scheduled(cron = "0 0 0 * * *")
    fun aggregate() {
        val yesterday = Duration.ofMillis(Date().time).toDays() - 1
        val sevenDaysAgo = yesterday - 6

        dailyAdStatRepo.findByDay(sevenDaysAgo).groupBy {
            Pair(it.adId, it.appId)
        }.toList().forEach {
            val weekStatRecord = weekStatRepo.findByAdIdAndAppId(adId = it.first.first, appId = it.first.second)
            weekStatRecord.get().clickCount -= it.second.sumBy { adStat -> adStat.clickCount }
            weekStatRecord.get().impressionCount -= it.second.sumBy { adStat -> adStat.impressionCount }
        }

        dailyAdStatRepo.findByDay(yesterday).groupBy {
            Pair(it.adId, it.appId)
        }.toList().forEach {
            val weekStatRecord = weekStatRepo.findByAdIdAndAppId(adId = it.first.first, appId = it.first.second)
            weekStatRecord.get().clickCount += it.second.sumBy { adStat -> adStat.clickCount }
            weekStatRecord.get().impressionCount += it.second.sumBy { adStat -> adStat.impressionCount }
        }

        logger.info("Aggregate last week statistics \t ${Date().time} \t ${Duration.ofMillis(Date().time).toDays()}")
    }


    // calls method at application startup in case there is no data in weekStat cassandra table
    // and there is data from last week in dailyStat table
    @PostConstruct
    fun initialAggregate() {
        val lastWeekDays = (1..6).map {
            Duration.ofMillis(Date().time).toDays() - it
        }

        dailyAdStatRepo.findByDayIn(lastWeekDays)
                .groupBy {
                    Pair(it.adId, it.appId)
                }.toList().forEach {
                    weekStatRepo.insertRecord(
                            adId = it.first.first,
                            appId = it.first.second,
                            impressionCount = it.second.sumBy { adStat -> adStat.impressionCount },
                            clickCount = it.second.sumBy { appStat -> appStat.clickCount })
                }

        println("aggregating on startup")
    }
}