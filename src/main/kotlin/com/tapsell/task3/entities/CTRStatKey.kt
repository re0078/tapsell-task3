package com.tapsell.task3.entities


class CTRStatRecord(
        val adId: String,
        val appId: String
) {

    override fun toString(): String {
        return "adId=$adId, appId=$appId"
    }

    fun stringOfAdId(): String {
        return "adId=$adId"
    }

    fun stringOfAppId(): String {
        return "appId=$appId"
    }

}