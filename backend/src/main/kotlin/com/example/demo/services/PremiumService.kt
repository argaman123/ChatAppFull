package com.example.demo.services

import com.example.demo.entities.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PremiumService @Autowired constructor(
    private val premiumDataService: PremiumDataService,
    private val premiumBackgroundService: PremiumBackgroundService
) {
    fun switchPlan(user: User, plan :String){
        premiumDataService.switchPlan(user, plan)
        premiumBackgroundService.updateBackgroundJobs(user)
    }
    fun renew(user: User){
        premiumDataService.renew(user)
        premiumBackgroundService.updateBackgroundJobs(user)
    }
}