package com.tapsell.task3.models

class ImpressionEvent(
        val requestId: String,
        val adID: String,
        val adTitle: String,
        val advertiserCost: Double,
        val appId: String,
        val appTitle: String,
        val impressionTime: Long
)