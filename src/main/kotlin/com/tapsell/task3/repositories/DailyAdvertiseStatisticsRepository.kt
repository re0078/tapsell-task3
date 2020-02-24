package com.tapsell.task3.repositories

import com.tapsell.task3.entities.DailyAdvertiseStatistics
import org.springframework.data.annotation.Id
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface DailyAdvertiseStatisticsRepository : CassandraRepository<DailyAdvertiseStatistics, String> {

    fun findByDay(day: Long) : List<DailyAdvertiseStatistics>

    fun findByDayAndAdId(day: Long, adId: String): List<DailyAdvertiseStatistics>

    fun findByDayAndAppId(day: Long, appId: String): List<DailyAdvertiseStatistics>

    fun findByDayAndAdIdAAndAppId(day: Long, adId: String, appId: String): List<DailyAdvertiseStatistics>
}