package com.tapsell.task3.services

import com.datastax.driver.core.querybuilder.QueryBuilder
import com.tapsell.task3.entities.DailyAdvertiseStatistics
import com.tapsell.task3.repositories.DailyAdvertiseStatisticsRepository
import com.tapsell.task3.repositories.WeekAdvertiseStatisticsRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.cassandra.core.CassandraTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*


@Service
class CTRQueryService(val dailyAdStatRepo: DailyAdvertiseStatisticsRepository,
                      val weekAdStatRepo: WeekAdvertiseStatisticsRepository,
                      val cassandraTemplate: CassandraTemplate) {


    private var today: Long = 0

    @Scheduled(fixedRate = 10000)
    fun startQuery() {
        today = Duration.ofMillis(Date().time).toDays()
        dailyAdStatRepo.findByDay(today).forEach {
            queryForAdId(it.adId)
            queryForAppId(it.appId)
            queryForAdIdAndAppId(it.adId, it.appId)
        }

        weekAdStatRepo.findAll().forEach {
            queryForAdId(it.adId)
            queryForAppId(it.appId)
            queryForAdIdAndAppId(it.adId, it.appId)
        }

    }


    @Cacheable(value = ["redis"], key = "#appId")
    fun queryForAppId(appId: String): Double {
        val todayStatList = cassandraTemplate.select(
                QueryBuilder.select().from("dailyStat")
                        .where(QueryBuilder.eq("appId", appId)).allowFiltering(),
                DailyAdvertiseStatistics::class.java)


        val todayImpressionCount = todayStatList.sumBy { stat -> stat.impressionCount }.toDouble()
        val todayClickCount = todayStatList.sumBy { stat -> stat.clickCount }

        val weekStatList = weekAdStatRepo.findByAppId(appId)
        val weekImpressionCount = weekStatList.sumBy { it.impressionCount }
        val weekClickCount = weekStatList.sumBy { it.clickCount }
        print("doing query .. ")
        return (todayClickCount + weekClickCount) / (todayImpressionCount + weekImpressionCount)
    }

    @Cacheable(value = ["redis"], key = "#adId")
    fun queryForAdId(adId: String): Double {
        val todayStatList = cassandraTemplate.select(
                QueryBuilder.select().from("dailyStat")
                        .where(QueryBuilder.eq("adId", adId)).allowFiltering(),
                DailyAdvertiseStatistics::class.java
        )
        val todayImpressionCount = todayStatList.sumBy { stat -> stat.impressionCount }.toDouble()
        val todayClickCount = todayStatList.sumBy { stat -> stat.clickCount }

        val weekStatList = weekAdStatRepo.findByAdId(adId)
        val weekImpressionCount = weekStatList.sumBy { it.impressionCount }
        val weekClickCount = weekStatList.sumBy { it.clickCount }

        return (todayClickCount + weekClickCount) / (todayImpressionCount + weekImpressionCount)
    }

    @Cacheable(value = ["redis"], key = "{#adId, #appId}")
    fun queryForAdIdAndAppId(adId: String, appId: String): Double {
        val todayStatList = cassandraTemplate.select(
                QueryBuilder.select().from("dailyStat")
                        .where(QueryBuilder.eq(arrayListOf("adId", "appId"), arrayListOf(adId, appId))).allowFiltering(),
                DailyAdvertiseStatistics::class.java
        )
        val todayImpressionCount = todayStatList.sumBy { stat -> stat.impressionCount }.toDouble()
        val todayClickCount = todayStatList.sumBy { stat -> stat.clickCount }

        val weekStatList = weekAdStatRepo.findByAdIdAndAppId(adId, appId)
        val weekImpressionCount = weekStatList.sumBy { it.impressionCount }
        val weekClickCount = weekStatList.sumBy { it.clickCount }

        return (todayClickCount + weekClickCount) / (todayImpressionCount + weekImpressionCount)
    }

}