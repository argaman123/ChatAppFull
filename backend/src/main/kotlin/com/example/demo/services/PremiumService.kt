package com.example.demo.services

import com.example.demo.entities.Premium
import com.example.demo.entities.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * A general premium service that provides common actions that a [Premium] account might take.
 * Calls both [PremiumDataService] and [PremiumBackgroundService] when needed.
 */
@Service
class PremiumService @Autowired constructor(
    private val premiumDataService: PremiumDataService,
    private val premiumBackgroundService: PremiumBackgroundService
) {
    /**
     * Switches the current [Premium] plan of a [User] to a new one, and schedules new background jobs if needed.
     * In cases where the plan has not changed, renews it instead.
     * @param[user] the user that wishes to change his [Premium] plan.
     * @param[plan] the name of the new plan that the [User] wants to switch to.
     */
    fun switchPlan(user: User, plan :String){
        premiumDataService.switchPlan(user, plan)
        premiumBackgroundService.updateBackgroundJobs(user)
    }

    /**
     * Renews the current [Premium] plan of a [User], and schedules new background jobs if needed.
     * @param[user] the said [User].
     */
    fun renew(user: User){
        premiumDataService.renew(user)
        premiumBackgroundService.updateBackgroundJobs(user)
    }
}