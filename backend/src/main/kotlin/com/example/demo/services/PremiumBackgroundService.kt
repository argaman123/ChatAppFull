package com.example.demo.services

import com.example.demo.entities.Premium
import com.example.demo.entities.User
import com.example.demo.models.RealUser
import com.example.demo.repositories.JobRepository
import com.example.demo.repositories.MessageRepository
import com.example.demo.repositories.PremiumRepository
import com.example.demo.repositories.UserRepository
import com.example.demo.static.freeUserMessageLimit
import com.example.demo.static.timeToRemindTheUserToRenew
import org.jobrunr.jobs.annotations.Job
import org.jobrunr.scheduling.JobScheduler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*
import com.example.demo.entities.Job as JobEntity

/**
 * A service that schedules background jobs for everything that has to do with [Premium] accounts, and is responsible for
 * keeping track of them and delete those who are not needed anymore.
 */
@Service
class PremiumBackgroundService @Autowired constructor(
    private val jobScheduler: JobScheduler,
    private val emailService: EmailService,
    private val premiumDataService: PremiumDataService,
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val premiumRepository: PremiumRepository,
    private val jobRepository: JobRepository,
    private val notificationService: NotificationService
) {

    // Unfortunately that's the boilerplate needed each time you create a new job that runs a service function,
    // in cases where you want to keep more information with it, so you could find it more easily in the future:
    // 1 - generate a unique id
    // 2 - pass the id as a parameter to the service function itself right when you schedule it
    // 3 - store a new JobEntity inside the database that includes the jobID plus the extra information
    // 4 - inside the service function delete the job from the database using the id
    // So Instead, I used a pretty unintuitive workaround, where I created a service function that will run all the other
    // ones- based of a string that represents the function name. That way I can make a function that will run all the boilerplate
    // in an abstract way for all functions.

    /**
     * An abstract [Job] that basically runs all the other real [Job]s.
     * @param[selfID] the "selfID" of the current running job on the [JobRepository], which allows it to delete itself
     * from the database as soon as it begins the job.
     * @param[name] the name of the function that the job is supposed to run.
     * @param[params] an array of all params the said function requires. Usually the first parameter will be the email
     * of the user, so that the function will be able to find the [User], but that might not always be the case.
     */
    @Transactional
    @Job(name = "Running %1(%2)")
    fun runServiceFunction(selfID: String, name: String, params: Array<Any>) {
        jobRepository.deleteBySelfID(selfID)
        when (name) {
            "emailRenewWarning" -> emailRenewWarning(params[0] as String)
            "renewSubscription" -> renewSubscription(params[0] as String)
            "removePremium" -> removePremium(params[0] as String)
        }
    }

    /**
     * Schedules a background [Job] to run a certain function at a certain time. It generates a unique selfID and passes
     * it along with all the other information to [runServiceFunction].
     * @param[at] at which time the function should run.
     * @param[name] the name of the function that the job will run.
     * @param[params] any amount of params that the said function requires.
     */
    fun runJob(at: Instant, name: String, vararg params: Any) {
        val selfID = UUID.randomUUID().toString()
        val actualParams = arrayOf(*params)
        val jobID = jobScheduler.schedule(at) {
            runServiceFunction(selfID, name, actualParams)
        }.asUUID().toString()
        jobRepository.saveAndFlush(JobEntity(selfID = selfID, jobID = jobID, email = params[0] as String, type = name))
    }

    /**
     * Makes sure all the running background jobs are up-to-date with the current [User]'s [Premium] plan, by deleting the
     * currently scheduled ones, and scheduling new ones.
     * Also, if a notification that was sent to a [RealUser] is no longer valid, it deletes it.
     * @param[user] the said [User].
     */
    fun updateBackgroundJobs(user: User) {
        user.premium?.let {
            deleteOldPlanJobs(user.email)
            when (it.plan) {
                "subscription" -> {
                    notificationService.deleteOldWarnings(user.email)
                    runJob(it.expiration.toInstant(), "renewSubscription", user.email)
                }
                "one-month" -> {
                    runJob(it.expiration.toInstant().minus(timeToRemindTheUserToRenew), "emailRenewWarning", user.email)
                    runJob(it.expiration.toInstant(), "removePremium", user.email)
                }
                else -> {
                    // error I guess
                }
            }
        }
    }

    /**
     * Stops and deletes all the jobs that are currently scheduled for a certain [RealUser].
     * @param[email] the email of the [RealUser].
     */
    fun deleteOldPlanJobs(email: String) {
        for (job in jobRepository.findByEmail(email)) {
            println("JOB DELETED: ${job.type}")
            jobScheduler.delete(UUID.fromString(job.jobID))
            jobRepository.delete(job)
        }
    }

    /**
     * Sends an email and a notification to a [RealUser], warning him that if he won't renew his current plan soon enough,
     * his messages will be deleted.
     * @param[email] the email of the [RealUser].
     */
    fun emailRenewWarning(email: String) {
        println("emailRenewWarning($email)")
        emailService.sendRenewWarning(email)
        notificationService.notifyRenewWarning(email)
    }

    /**
     * Renews a [Premium] plan of a subscribed [RealUser] and letting him know via email and notification.
     * Also, it already scheduled the same job to run when the current premium plan will expire.
     * @param[email] the email of the [RealUser].
     */
    fun renewSubscription(email: String) {
        println("renewSubscription($email)")
        userRepository.findByEmail(email)?.let {
            premiumDataService.renew(it)
            emailService.sendAutomaticallyRenewedNotification(email)
            notificationService.notifyRenewedAutomatically(email)
            updateBackgroundJobs(it)
        }
    }

    /**
     * Removes the expired [Premium] plan of a [RealUser], and with that deletes all the messages that surpass
     * the [freeUserMessageLimit].
     * @param[email] the email of the [RealUser].
     */
    fun removePremium(email: String) {
        println("removePremium($email)")
        userRepository.findByEmail(email)?.let {
            it.premium?.let { premium ->
                val id = premium.id!!
                it.premium = null
                userRepository.saveAndFlush(it)
                premiumRepository.deleteById(id)
            }
            val messages = messageRepository.findByEmailOrderByDatetimeDesc(email)
            for (i in freeUserMessageLimit until messages.size step 1) {
                messageRepository.delete(messages[i])
            }
            notificationService.deleteOldWarnings(email)
        }
    }

}