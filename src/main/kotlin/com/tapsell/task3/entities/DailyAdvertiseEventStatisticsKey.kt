package com.tapsell.task3.entities

import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import java.io.Serializable

// What's the purpose of this class? It seems like a useful feature ...
@PrimaryKeyClass
class DailyAdvertiseEventStatisticsKey(
        @PrimaryKeyColumn(name = "day")
        val day: Long,
        @PrimaryKeyColumn
        val adId: String,
        @PrimaryKeyColumn
        val appId: String
) : Serializable