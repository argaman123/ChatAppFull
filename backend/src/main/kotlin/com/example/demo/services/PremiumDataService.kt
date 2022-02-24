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

/**
 * A service that provides common functions that a [Premium] user need, and is responsible for storing all the information
 * in the database.
 */
@Service
class PremiumDataService @Autowired constructor(
    private val premiumRepository: PremiumRepository,
    private val userRepository: UserRepository,
    private val emailService: EmailService,
    private val notificationService: NotificationService
) {

    /**
     * Renews the current [Premium] plan of a [User], and makes sure to invalidate the renewal URL, and delete the
     * now-"deprecated" warning notification that was sent to the user.
     * @param[user] the said [User].
     */
    fun renew(user: User) {
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
            notificationService.deleteOldWarnings(user.email)
        }
    }

    /**
     * Generates a new [Premium] object for a user based on the chosen plan.
     * @param[plan] the name of the [Premium] plan that the [User] chose.
     * @param[user] the [User] that the [Premium] plan is for.
     * @return if the name of the plan is valid returns a new [Premium] object, otherwise returns null.
     */
    private fun createPremium(plan: String, user: User): Premium? {
        return when (plan) {
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

    /**
     * Switches the current [Premium] plan of a [User] to a new one.
     * In cases where the plan has not changed, renews it instead.
     * @param[user] the user that wishes to change his [Premium] plan.
     * @param[plan] the name of the new plan that the [User] wants to switch to.
     */
    fun switchPlan(user: User, plan: String) {
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