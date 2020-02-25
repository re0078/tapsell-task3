package com.tapsell.task3.controllers

import com.tapsell.task3.entities.CTRStatRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class Test(val redisTemplate: RedisTemplate<String, Double>) {

    @RequestMapping("/test")
    @ResponseBody
    fun test(@RequestBody record: CTRStatRecord): String {
        val ops = redisTemplate.opsForValue()
        return "for both ${ops[record.toString()]}\n" +
                "for adId ${ops[record.stringOfAdId()]}\n " +
                "for appId ${ops[record.stringOfAppId()]}"
    }
}