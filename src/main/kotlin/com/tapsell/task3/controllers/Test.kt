package com.tapsell.task3.controllers

import com.tapsell.task3.services.CTRQueryService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.time.Duration
import java.util.*

@Controller
class Test(val ctrQueryService: CTRQueryService) {

    @RequestMapping("/test")
    @ResponseBody
    fun test(): Double {
        println("${Duration.ofMillis(Date().time).toDays()} this is today")
        return ctrQueryService.queryForAdIdAndAppId("1", "6")
    }
}