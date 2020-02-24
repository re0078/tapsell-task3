package com.tapsell.task3.controllers

import com.tapsell.task3.services.CTRQueryService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class Test(val ctrQueryService: CTRQueryService) {

//    @RequestMapping("/test")
//    fun test(): Double {
//        return ctrQueryService.queryForAdId("1")
//    }
}