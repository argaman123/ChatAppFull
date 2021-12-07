package com.example.demo.controller

import com.example.demo.repository.MessageRepository
import com.example.demo.repository.PremiumRepository
import com.example.demo.repository.UserRepository
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api2")
class AppController (
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val PremiumRepository: PremiumRepository
){
}