package com.tapsell.task3.models

class ClickEvent(
        val requestId: String,
        val impressionTime: Long,
        val clickTime: Long,
        val adId: String,
        val appId: String
)