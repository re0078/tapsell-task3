package com.tapsell.task3.controllers

import com.tapsell.task3.models.ClickEvent
import com.tapsell.task3.services.ClickEventService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/click")
class ClickEventController(val clickService: ClickEventService) {


    @PostMapping("/")
    fun clickEvent(@RequestBody clickEvent: ClickEvent): String {
        clickService.pushClickEvent(clickEvent)
        return "click data received"
    }
}
