package com.tapsell.task3.entities

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table(value = "weekStat")
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