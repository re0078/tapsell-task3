package com.tapsell.task3.models

class ClickEvent(
        val requestId: String,
        val impressionTime: Long,
        val clickTime: Long,
        val adID: String,
        val appId: String
)