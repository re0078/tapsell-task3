package com.tapsell.task3.repositories

import com.tapsell.task3.entities.WeekAdvertiseStatistics
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface WeekAdvertiseStatisticsRepository : CassandraRepository<WeekAdvertiseStatistics, String> {

    fun findByAdId(adId: String): List<WeekAdvertiseStatistics>

    fun findByAppId(appId: String): List<WeekAdvertiseStatistics>

    fun findByAdIdAndAppId(adId: String, appId: String): List<WeekAdvertiseStatistics>
}