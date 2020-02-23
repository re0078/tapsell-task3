package com.tapsell.task3.controllers

import com.tapsell.task3.models.ImpressionEvent
import com.tapsell.task3.services.ImpressionEventService
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/impression")
class ImpressionEventController(val impressionEventService: ImpressionEventService) {

    @RequestMapping("/")
    fun add(@RequestBody impressionEvent: ImpressionEvent): String {
        impressionEventService.pushImpressionEv(impressionEvent)
        return "impression data received"
    }
}