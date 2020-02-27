package com.tapsell.task3.services

import com.tapsell.task3.configurations.redisConfig.CTRTimeTOLive
import com.tapsell.task3.entities.CTRStatRecord
import com.tapsell.task3.entities.DailyAdvertiseStatistics
import com.tapsell.task3.entities.WeekAdvertiseStatistics
import com.tapsell.task3.repositories.DailyAdvertiseStatisticsRepository
import com.tapsell.task3.repositories.WeekAdvertiseStatisticsRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit


@Service
class CTRQueryService(val dailyAdStatRepo: DailyAdvertiseStatisticsRepository,
                      val weekAdStatRepo: WeekAdvertiseStatisticsRepository,
                      val redisTemplate: RedisTemplate<String, Double>) {


    private var today: Long = 0

    @Scheduled(fixedRate = 1800000) // 30 minutes
    fun startQuery() {
        today = Duration.ofMillis(Date().time).toDays()
        dailyAdStatRepo.findByDay(today).forEach { test(CTRStatRecord(it.adId, it.appId)) }

        weekAdStatRepo.findAll().forEach { test(CTRStatRecord(it.adId, it.appId)) }
    }

    fun test(keyObject: CTRStatRecord) {
        if (redisTemplate.opsForValue().get(keyObject.stringOfAdId()) == null)
            setRecords(key = keyObject.stringOfAdId(),
                    todayStatList = dailyAdStatRepo.findByDayAndAdId(today, adId = keyObject.adId),
                    weekStatList = weekAdStatRepo.findByAdId(keyObject.adId))
        if (redisTemplate.opsForValue().get(keyObject.stringOfAppId()) == null)
            setRecords(key = keyObject.stringOfAppId(),
                    todayStatList = dailyAdStatRepo.findByDayAndAppId(today, appId = keyObject.appId),
                    weekStatList = weekAdStatRepo.findByAppId(keyObject.appId))
        if (redisTemplate.opsForValue().get(keyObject.toString()) == null)
            setRecords(key = keyObject.toString(),
                    todayStatList = listOf(dailyAdStatRepo.findByDayAndAdIdAndAppId(today, adId = keyObject.adId, appId = keyObject.appId).get()),
                    weekStatList = listOf(weekAdStatRepo.findByAdIdAndAppId(adId = keyObject.adId, appId = keyObject.appId).get()))
    }

    fun setRecords(key: String, todayStatList: List<DailyAdvertiseStatistics>, weekStatList: List<WeekAdvertiseStatistics>) {
        val todayImpressionCount = todayStatList.sumBy { stat -> stat.impressionCount }.toDouble()
        val todayClickCount = todayStatList.sumBy { stat -> stat.clickCount }
        val weekImpressionCount = weekStatList.sumBy { it.impressionCount }
        val weekClickCount = weekStatList.sumBy { it.clickCount }

        val ctrValue = (todayClickCount + weekClickCount) / (todayImpressionCount + weekImpressionCount)
        redisTemplate.opsForValue().set(key, ctrValue, CTRTimeTOLive.IN_MILLIS, TimeUnit.MILLISECONDS)
    }
}