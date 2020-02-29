package com.tapsell.task3.repositories

import com.tapsell.task3.entities.DailyAdvertiseStatistics
import org.springframework.data.cassandra.repository.CassandraRepository
import com.tapsell.task3.configurations.cassandraConfig.CassandraConfiguration.TimeToLive
import com.tapsell.task3.configurations.cassandraConfig.CassandraConfiguration.TableNames
import com.tapsell.task3.configurations.cassandraConfig.KEY_SPACE_NAME
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface DailyAdvertiseStatisticsRepository : CassandraRepository<DailyAdvertiseStatistics, String> {

    fun findByDay(day: Long): List<DailyAdvertiseStatistics>

    fun findByDayIn(days: List<Long>): List<DailyAdvertiseStatistics>

    @Query(value = "INSERT INTO ${KEY_SPACE_NAME}.${TableNames.DAILY_STAT} (day, adId, appId, impressionCount, clickCount)" +
            " VALUES (?0, ?1, ?2, ?3, ?4) USING ttl ${TimeToLive.FOR_DAILY_STAT}") // time to live set to 8 days
    fun insertRecord(day: Long, adId: String, appId: String, impressionCount: Int, clickCount: Int)

    @Query(value = "SELECT * FROM ${TableNames.DAILY_STAT} WHERE day=?0 AND adId=?1")
    fun findByDayAndAdId(day: Long, adId: String): List<DailyAdvertiseStatistics>

    @Query(value = "SELECT * FROM ${TableNames.DAILY_STAT} WHERE day=?0 AND adId=?1 AND appId=?2")
    fun findByDayAndAdIdAndAppId(day: Long, adId: String, appId: String): Optional<DailyAdvertiseStatistics>

}