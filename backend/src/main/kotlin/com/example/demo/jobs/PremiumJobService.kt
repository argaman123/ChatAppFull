package com.example.demo.jobs

import com.example.demo.entities.User
import org.jobrunr.jobs.JobId
import org.jobrunr.jobs.annotations.Job
import org.jobrunr.scheduling.JobScheduler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class PremiumJobService @Autowired constructor(
    private val jobScheduler: JobScheduler
) {

    fun registerNewPremiumUser(user: User) {
        user.premium?.let {
            when (it.plan) {
                "subscription" -> {
                    jobScheduler.schedule(it.expiration.toInstant()) {
                        renewSubscription(user.email)
                    }
                }
                "one-month" -> {
                    val deletionID = jobScheduler.schedule(it.expiration.toInstant()) {
                        removePremium(user.email)
                    }.asUUID()
                    jobScheduler.schedule(it.expiration.toInstant().minus(5, ChronoUnit.MINUTES)) {
                        emailRenewWarning(user.email, deletionID)
                    }
                }
                else -> {
                    // error I guess
                }
            }
        }
    }

    @Job(name = "Email %0 that he needs to renew")
    fun emailRenewWarning(email: String, deletionID: UUID) {
        println("emailRenewWarning($email)")
    }

    @Job(name = "Renew %0 subscription, and email him")
    fun renewSubscription(email: String) {
        println("renewSubscription($email)")
    }

    @Job(name = "Deletes %0 messages because he is no longer premium")
    fun removePremium(email: String) {
        println("deleteNonPremiumMessages($email)")
    }

}