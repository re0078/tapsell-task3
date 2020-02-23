package com.tapsell.task3.entities

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import java.io.Serializable


@PrimaryKeyClass
class DailyAdvertiseEventStatisticsKey(
        @PrimaryKeyColumn(name = "day")
        val day: Long,
        @PrimaryKeyColumn
        val adId: String,
        @PrimaryKeyColumn
        val appId: String
) : Serializable