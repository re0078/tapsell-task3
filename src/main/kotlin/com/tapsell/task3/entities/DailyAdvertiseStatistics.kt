package com.tapsell.task3.entities

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table


@Table(value = "dailyStat")
class DailyAdvertiseStatistics(
        @PrimaryKey(value = "")
        val day: Long,
        // Why aren't addId and appId among primary keys?
        @Column
        val adId: String,
        @Column
        val appId: String,
        @Column
        var impressionCount: Int,
        @Column
        var clickCount: Int
)