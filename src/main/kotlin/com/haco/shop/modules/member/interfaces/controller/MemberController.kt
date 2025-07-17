package com.haco.shop.modules.member.interfaces.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member")
class MemberController {

    @GetMapping
    fun getMemberDetails() {}
}