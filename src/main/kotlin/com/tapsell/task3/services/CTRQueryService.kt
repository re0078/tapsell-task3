package com.tapsell.task3.services

import com.tapsell.task3.entities.CTRStatRecord
import com.tapsell.task3.repositories.DailyAdvertiseStatisticsRepository
import com.tapsell.task3.repositories.WeekAdvertiseStatisticsRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*


@Service
class CTRQueryService(val dailyAdStatRepo: DailyAdvertiseStatisticsRepository,
                      val weekAdStatRepo: WeekAdvertiseStatisticsRepository,
                      val redisTemplate: RedisTemplate<String, Double>) {


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


    fun queryForAdId(adId: String) {
        val todayStatList = dailyAdStatRepo.findByDayAndAdId(today, adId)

        val todayImpressionCount = todayStatList.sumBy { stat -> stat.impressionCount }.toDouble()
        val todayClickCount = todayStatList.sumBy { stat -> stat.clickCount }

        val weekStatList = weekAdStatRepo.findByAdId(adId)
        val weekImpressionCount = weekStatList.sumBy { it.impressionCount }
        val weekClickCount = weekStatList.sumBy { it.clickCount }

        redisTemplate.opsForValue().set(CTRStatRecord(adId, null).stringOfAdId(),
                (todayClickCount + weekClickCount) / (todayImpressionCount + weekImpressionCount))
    }

    @Cacheable(value = ["redis"], key = "#appId")
    fun queryForAppId(appId: String) {
        val todayStatList = dailyAdStatRepo.findByDayAndAppId(today, appId)

        val todayImpressionCount = todayStatList.sumBy { stat -> stat.impressionCount }.toDouble()
        val todayClickCount = todayStatList.sumBy { stat -> stat.clickCount }

        val weekStatList = weekAdStatRepo.findByAppId(appId)
        val weekImpressionCount = weekStatList.sumBy { it.impressionCount }
        val weekClickCount = weekStatList.sumBy { it.clickCount }

        redisTemplate.opsForValue().set(CTRStatRecord(null, appId).stringOfAppId(),
                (todayClickCount + weekClickCount) / (todayImpressionCount + weekImpressionCount))
    }

    @Cacheable(value = ["redis"], key = "{#adId, #appId}")
    fun queryForAdIdAndAppId(adId: String, appId: String) {
        val todayStatList = dailyAdStatRepo.findByDayAndAdIdAndAppId(today, adId, appId)
        val todayImpressionCount = todayStatList.sumBy { stat -> stat.impressionCount }.toDouble()
        val todayClickCount = todayStatList.sumBy { stat -> stat.clickCount }

        val weekStatList = weekAdStatRepo.findByAdIdAndAppId(adId, appId)
        val weekImpressionCount = weekStatList.sumBy { it.impressionCount }
        val weekClickCount = weekStatList.sumBy { it.clickCount }
        println("doing queries for appID and ad ID..")
        redisTemplate.opsForValue().set(CTRStatRecord(adId, appId).toString(),
                (todayClickCount + weekClickCount) / (todayImpressionCount + weekImpressionCount))
    }

}