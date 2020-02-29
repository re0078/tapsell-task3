package com.tapsell.task3.repositories

import com.tapsell.task3.configurations.cassandraConfig.CassandraConfiguration.*
import com.tapsell.task3.configurations.cassandraConfig.KEY_SPACE_NAME
import com.tapsell.task3.entities.WeekAdvertiseStatistics
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WeekAdvertiseStatisticsRepository : CassandraRepository<WeekAdvertiseStatistics, String> {


    @Query(value = "INSERT INTO ${KEY_SPACE_NAME}.${TableNames.WEEK_STAT} (adId, appId, impressionCount, clickCount)" +
            " VALUES (?0, ?1, ?2, ?3) USING ttl ${TimeToLive.FOR_WEEK_STAT}") // set to one day (time to live)
    fun insertRecord(adId: String, appId: String, impressionCount: Int, clickCount: Int)

    @Query(value = "SELECT * FROM ${TableNames.WEEK_STAT} WHERE adId=?0")
    fun findByAdId(adId: String): List<WeekAdvertiseStatistics>

    @Query(value = "SELECT * FROM ${TableNames.WEEK_STAT} WHERE adId=?0 AND appId=?1")
    fun findByAdIdAndAppId(adId: String, appId: String): Optional<WeekAdvertiseStatistics>
}