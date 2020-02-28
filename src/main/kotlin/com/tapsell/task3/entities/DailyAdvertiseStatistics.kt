package com.tapsell.task3.entities

import com.tapsell.task3.configurations.cassandraConfig.CassandraConfiguration.TableNames
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table


@Table(value = TableNames.DAILY_STAT)
class DailyAdvertiseStatistics(
        @PrimaryKey
        val day: Long,
        @Column
        val adId: String,
        @Column
        val appId: String,
        @Column
        var impressionCount: Int,
        @Column
        var clickCount: Int
)