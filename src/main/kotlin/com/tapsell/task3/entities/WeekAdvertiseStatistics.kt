package com.tapsell.task3.entities

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import com.tapsell.task3.configurations.cassandraConfig.CassandraConfiguration.TableNames

@Table(value = TableNames.WEEK_STAT)
class WeekAdvertiseStatistics(
        @PrimaryKey
        val adId: String,
        @Column
        val appId: String,
        @Column
        var impressionCount: Int,
        @Column
        var clickCount: Int
)