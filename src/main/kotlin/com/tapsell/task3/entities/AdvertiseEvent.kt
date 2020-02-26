package com.tapsell.task3.entities

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table(value = "adEvent")
class AdvertiseEvent(
        @PrimaryKey
        val requestID: String,
        @Column
        var adId: String,
        @Column
        var adTitle: String,
        @Column(value = "advertiseCost")
        var advertiserCost: Double,
        @Column
        var appId: String,
        @Column
        var appTitle: String,
        @Column
        var impressionTime: Long,
        @Column
        var clickTime: Long?
)
