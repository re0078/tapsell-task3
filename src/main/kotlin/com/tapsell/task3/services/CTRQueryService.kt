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
import javax.annotation.PostConstruct


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
        val weekStatListByAdId = weekAdStatRepo.findByAdId(keyObject.adId)
        val weekStatByAdIdAndApID = weekStatListByAdId.find { it.appId == keyObject.appId }
        val weekStatListByAppId = weekAdStatRepo.findAll().filter { it.appId == keyObject.appId }

        val todayStatListByAdId = dailyAdStatRepo.findByDayAndAdId(today, keyObject.adId)
        val todayStatByAdIdAndAppID = todayStatListByAdId.find { it.appId == keyObject.appId }
        val todayStatListByAppId = dailyAdStatRepo.findByDay(today).filter { it.appId == keyObject.appId }

        setRecords(key = keyObject.stringOfAdId(),
                todayStatList = todayStatListByAdId,
                weekStatList = weekStatListByAdId)

        setRecords(key = keyObject.stringOfAppId(),
                todayStatList = todayStatListByAppId,
                weekStatList = weekStatListByAppId)

        setRecords(key = keyObject.toString(),
                todayStatList = if (todayStatByAdIdAndAppID != null) listOf(todayStatByAdIdAndAppID) else listOf(),
                weekStatList = if (weekStatByAdIdAndApID != null) listOf(weekStatByAdIdAndApID) else listOf())
    }

    fun setRecords(key: String, todayStatList: List<DailyAdvertiseStatistics>, weekStatList: List<WeekAdvertiseStatistics>) {
        val todayImpressionCount = todayStatList.sumBy { stat -> stat.impressionCount }.toDouble()
        val todayClickCount = todayStatList.sumBy { stat -> stat.clickCount }.toDouble()
        val weekImpressionCount = weekStatList.sumBy { it.impressionCount }.toDouble()
        val weekClickCount = weekStatList.sumBy { it.clickCount }.toDouble()
        val ctrValue = (todayClickCount + weekClickCount) / (todayImpressionCount + weekImpressionCount)
        redisTemplate.opsForValue().set(key, ctrValue, CTRTimeTOLive.IN_MILLIS, TimeUnit.MILLISECONDS)
    }


    @PostConstruct
    fun deleteAllKeys() {
        redisTemplate.connectionFactory?.connection?.flushAll()
    } // deleting all keys from all dbs in redis

}