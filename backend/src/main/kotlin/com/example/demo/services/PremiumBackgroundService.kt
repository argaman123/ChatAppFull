package com.example.demo.services

import com.example.demo.entities.User
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
import java.time.temporal.ChronoUnit
import java.util.*
import com.example.demo.entities.Job as JobEntity

@Service
class PremiumBackgroundService @Autowired constructor(
    private val jobScheduler: JobScheduler,
    private val emailService: EmailService,
    private val premiumService: PremiumService,
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val premiumRepository: PremiumRepository,
    private val jobRepository: JobRepository
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

    @Transactional
    @Job(name = "Running %1(%2)")
    fun runServiceFunction(selfID: String, name: String, params: Array<Any>){
        jobRepository.deleteBySelfID(selfID)
        when(name){
            "emailRenewWarning" -> emailRenewWarning(params[0] as String)
            "renewSubscription" -> renewSubscription(params[0] as String)
            "removePremium" -> removePremium(params[0] as String)
        }
    }

    fun runJob(at: Instant, name: String, vararg params: Any){
        val selfID = UUID.randomUUID().toString()
        val actualParams = arrayOf(*params)
        val jobID = jobScheduler.schedule(at) {
            runServiceFunction(selfID, name, actualParams)
        }.asUUID().toString()
        jobRepository.saveAndFlush(JobEntity(selfID = selfID, jobID = jobID, email = params[0] as String, type = name))
    }

    fun updateBackgroundJobs(user: User) {
        user.premium?.let {
            deleteOldPlanJobs(user.email)
            when (it.plan) {
                "subscription" -> {
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

    fun deleteOldPlanJobs(email :String){
        for (job in jobRepository.findByEmail(email)) {
            println("JOB DELETED: ${job.type}")
            jobScheduler.delete(UUID.fromString(job.jobID))
            jobRepository.delete(job)
        }
    }

    @Job(name = "Email %0 that he needs to renew")
    fun emailRenewWarning(email: String) {
        println("emailRenewWarning($email)")
        emailService.sendRenewWarning(email)
    }

    @Job(name = "Renew %0 subscription, and email him")
    fun renewSubscription(email: String) {
        println("renewSubscription($email)")
        userRepository.findByEmail(email)?.let {
            premiumService.renew(it)
            emailService.sendRenewedNotification(email)
        }
        userRepository.findByEmail(email)?.let {
            updateBackgroundJobs(it)
        }

        // delete from database
    }

    @Job(name = "Delete %0 messages because he is no longer premium")
    fun removePremium(email: String) {
        println("removePremium($email)")
        userRepository.findByEmail(email)?.let {
            it.premium?.let { premium ->
                val id = premium.id!!
                it.premium = null
                userRepository.saveAndFlush(it)
                premiumRepository.deleteById(id)
            }
        }
        val messages = messageRepository.findByEmailOrderByDatetimeDesc(email)
        for (i in freeUserMessageLimit until messages.size step 1) {
            messageRepository.delete(messages[i])
        }
    }

/*
    fun test(email: String) {
        println(userRepository.findByEmail(email)?.nickname)
    }

    fun testJob() {
        runJob(Instant.now().plus(15, ChronoUnit.SECONDS), "test", "argaman48@gmail.com")
    }
    fun schedule(at: Instant, email: String, name: String, run: (() -> Unit)) {
        val selfID = UUID.randomUUID().toString()
        val jobID = jobScheduler.schedule(at) {
            //runner(selfID)
        }.asUUID().toString()
        jobRepository.saveAndFlush(
            JobEntity(
                selfID = selfID, jobID = jobID, type = name, email = email
            )
        )
    }


    fun runner(id :String, func: () -> Unit){
        println(id)
        func()
    }

    fun testJob() {

val selfID2 = generateUUID()
        val jobID2 = jobScheduler.schedule(Instant.now().plus(15, ChronoUnit.SECONDS)) {
            test2("argaman48@gmail.com", selfID)
        }.asUUID().toString()
        jobRepository.saveAndFlush(
            JobEntity(
                selfID = selfID2, jobID = jobID2, type = "test2gfdgdfgfdgdf", email = "argaman48@gmail.com"
            )
        )


        val selfID = generateUUID() // 1 - generate unique id
        val jobID = jobScheduler.schedule(Instant.now().plus(30, ChronoUnit.SECONDS)) {
            runner(selfID, ::test3) // 2 - pass the id to the function
        }.asUUID().toString()
        // 3 - save the job in the database
        jobRepository.saveAndFlush(
            JobEntity(
                selfID = selfID,
                jobID = jobID,
                type = "test1dsfdfdsfds",
                email = "argaman48@gmail.com"
            )
        )
    }

    @Transactional
    @Job(name = "test3")
    fun test3() {
        println("test3")
    }

    @Transactional
    @Job(name = "test(%0, %1)")
    fun test(email: String, self: String) {
        jobRepository.deleteBySelfID(self) // 4 - delete the job from the database
        println(userRepository.findByEmail(email)?.nickname)
    }

    @Transactional
    @Job(name = "test(%0, %1)")
    fun test2(email: String, self: String) {
        jobRepository.deleteBySelfID(self)
        for (job in jobRepository.findByEmailAndTypeContaining(email, "test1")) {
            println("JOB DELETED ${job.type}")
            jobScheduler.delete(UUID.fromString(job.jobID))
            jobRepository.delete(job)
        }
        println(userRepository.findByEmail(email)?.nickname)
    }
*/

}