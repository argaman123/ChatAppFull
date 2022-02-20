package com.example.demo.services

import com.example.demo.entities.Premium
import com.example.demo.entities.User
import com.example.demo.repositories.PremiumRepository
import com.example.demo.repositories.UserRepository
import com.example.demo.static.premiumMonthPeriod
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class PremiumDataService @Autowired constructor(
    private val premiumRepository: PremiumRepository,
    private val userRepository: UserRepository,
    private val emailService: EmailService
    ){

    fun renew(user: User){
        user.premium?.let {
            var newExp = Instant.now()
            val originalExp = user.premium?.expiration?.toInstant()
            println("${Date.from(newExp)}|${Date.from(originalExp)}")
            if (newExp.isBefore(originalExp)) {
                newExp = originalExp
                println(Date.from(newExp))
            }
            newExp = newExp.plus(premiumMonthPeriod)
            println(Date.from(newExp))
            it.expiration = Date.from(newExp)
            premiumRepository.saveAndFlush(user.premium!!)
            userRepository.saveAndFlush(user)
            emailService.forgetRenewUrl(user.email)
        }
    }

    fun createPremium(plan: String, user: User) :Premium?{
        return when(plan){
            "subscription", "one-month" -> {
                val expiration = Instant.now().plus(premiumMonthPeriod)
                Premium(expiration = Date.from(expiration), plan = plan, user = user)
            }
            "none" -> {
                null
            }
            else -> {
                null
            }
        }
    }

    fun switchPlan(user: User, plan: String){
        user.premium?.let {
            if (it.plan == plan) return renew(user)
            it.plan = plan
        } ?: run {
            user.premium = createPremium(plan, user)
        }
        userRepository.saveAndFlush(user)
        user.premium?.let { premiumRepository.saveAndFlush(it) }
    }
}