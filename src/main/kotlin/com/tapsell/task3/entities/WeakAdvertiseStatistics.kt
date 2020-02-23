package com.tapsell.task3.entities

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table
class WeakAdvertiseStatistics (
        @PrimaryKey
        val adId:String,
        @Column
        val appId: String,
        @Column
        val ImpressionCount: Int,
        @Column
        val clickCount: Int
)