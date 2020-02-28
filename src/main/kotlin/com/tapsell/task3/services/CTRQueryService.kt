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

    @Scheduled(fixedRate = 1800000, initialDelay = 10000) // 30 minutes
    fun startQuery() {
        println("doing ctr queries")
        today = Duration.ofMillis(Date().time).toDays()
        dailyAdStatRepo.findByDay(today).forEach { findMatchingStats(CTRStatRecord(it.adId, it.appId)) }

        weekAdStatRepo.findAll().forEach { findMatchingStats(CTRStatRecord(it.adId, it.appId)) }
    }

    fun findMatchingStats(keyObject: CTRStatRecord) {
        if (redisTemplate.opsForValue().get(keyObject.stringOfAdId()) == null)
            setRecords(key = keyObject.stringOfAdId(),
                    todayStatList = dailyAdStatRepo.findByDayAndAdId(today, adId = keyObject.adId),
                    weekStatList = weekAdStatRepo.findByAdId(keyObject.adId))
        if (redisTemplate.opsForValue().get(keyObject.stringOfAppId()) == null)
            setRecords(key = keyObject.stringOfAppId(),
                    todayStatList = dailyAdStatRepo.findByDayAndAppId(today, appId = keyObject.appId),
                    weekStatList = weekAdStatRepo.findByAppId(keyObject.appId))
        if (redisTemplate.opsForValue().get(keyObject.toString()) == null) {
            val dailyRecord = dailyAdStatRepo.findByDayAndAdIdAndAppId(today, adId = keyObject.adId, appId = keyObject.appId)
            val weekRecord = weekAdStatRepo.findByAdIdAndAppId(adId = keyObject.adId, appId = keyObject.appId)
            setRecords(key = keyObject.toString(),
                    todayStatList = if (dailyRecord.isPresent) listOf(dailyRecord.get()) else listOf(),
                    weekStatList = if (weekRecord.isPresent) listOf(weekRecord.get()) else listOf()
            )
        }
    }

    fun setRecords(key: String, todayStatList: List<DailyAdvertiseStatistics>, weekStatList: List<WeekAdvertiseStatistics>) {
        val todayImpressionCount = todayStatList.sumBy { stat -> stat.impressionCount }.toDouble()
        val todayClickCount = todayStatList.sumBy { stat -> stat.clickCount }.toDouble()
        val weekImpressionCount = weekStatList.sumBy { it.impressionCount }.toDouble()
        val weekClickCount = weekStatList.sumBy { it.clickCount }.toDouble()

        val ctrValue = (todayClickCount + weekClickCount) / (todayImpressionCount + weekImpressionCount)
        redisTemplate.opsForValue().set(key, ctrValue, CTRTimeTOLive.IN_MILLIS, TimeUnit.MILLISECONDS)
    }

//    @PostConstruct
//    fun deleteAllKeys() {
//        var j = 0
//        for (i in redisTemplate.keys(".+")) {
//            j += 1
//        }
//        println("number of keys found $j")
//        redisTemplate.delete(redisTemplate.keys(".+"))
//    }

    // todo create a method to clean redis
}