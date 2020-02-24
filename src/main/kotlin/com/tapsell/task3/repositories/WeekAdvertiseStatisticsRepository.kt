package com.tapsell.task3.repositories

import com.tapsell.task3.entities.WeekAdvertiseStatistics
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface WeekAdvertiseStatisticsRepository : CassandraRepository<WeekAdvertiseStatistics, String> {

    @Query(value = "SELECT * FROM weekStat WHERE adId=?0 ALLOW FILTERING")
    fun findByAdId(adId: String): List<WeekAdvertiseStatistics>

    @Query(value = "SELECT * FROM weekStat WHERE appId=?0 ALLOW FILTERING")
    fun findByAppId(appId: String): List<WeekAdvertiseStatistics>

    @Query(value = "SELECT * FROM weekStat WHERE adId=?0 AND appId=?1 ALLOW FILTERING")
    fun findByAdIdAndAppId(adId: String, appId: String): List<WeekAdvertiseStatistics>
}