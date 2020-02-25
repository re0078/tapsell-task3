package com.tapsell.task3.repositories

import com.tapsell.task3.entities.DailyAdvertiseStatistics
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface DailyAdvertiseStatisticsRepository : CassandraRepository<DailyAdvertiseStatistics, String> {

    fun findByDay(day: Long): List<DailyAdvertiseStatistics>

    fun findByDayIn(days: List<Long>): List<DailyAdvertiseStatistics>

    @Query(value = "INSERT INTO advertiseStat.dailyStat (day, adId, appId, impressionCount, clickCount)" +
            " VALUES (?0, ?1, ?2, ?3, ?4) USING ttl 86400") // time to live set to one day
    fun insertRecord(day: Long, adId: String, appId: String, impressionCount: Int, clickCount: Int)

    @Query(value = "SELECT * FROM dailyStat WHERE day=?0 AND adId=?1 ALLOW FILTERING")
    fun findByDayAndAdId(day: Long, adId: String): List<DailyAdvertiseStatistics>

    @Query(value = "SELECT * FROM dailyStat WHERE day=?0 AND appId=?1 ALLOW FILTERING")
    fun findByDayAndAppId(day: Long, appId: String): List<DailyAdvertiseStatistics>

    @Query(value = "SELECT * FROM dailyStat WHERE day=?0 AND adId=?1 AND appId=?2 ALLOW FILTERING")
    fun findByDayAndAdIdAndAppId(day: Long, adId: String, appId: String): List<DailyAdvertiseStatistics>

}