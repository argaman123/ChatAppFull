package com.example.demo.services

import com.example.demo.entities.Renew
import com.example.demo.repositories.RenewRepository
import com.example.demo.static.timeToRemindTheUserToRenew
import org.jobrunr.jobs.annotations.Job
import org.jobrunr.scheduling.JobScheduler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant


/**
 * A service for sending emails to users about renewing their premium plan, letting them know that it was automatically
 * renewed, etc.
 *
 * Uses [renewRepository] to keep track of all the renewal links that were generated and sent to users.
 */
@Service
class EmailService @Autowired constructor(
    private val javaMailSender: JavaMailSender,
    private val jobScheduler: JobScheduler,
    private val renewRepository: RenewRepository
) {

    /**
     * A scheduled background job that "invalidates" a renewal link, by deleting it from the [renewRepository].
     * @param[email] the email of the user that received the renewal link.
     */
    @Transactional
    @Job(name = "Invalidate the renew link")
    fun forgetRenewUrl(email: String){
        renewRepository.deleteByEmail(email)
    }

    /**
     * Generates a [Renew] object that contains a unique URL, saves it in the [renewRepository] and schedules a background
     * job that will later remove it from there.
     * @param[email] the email of the user that the URL will renew his premium plan.
     * @return the URL that will be later sent to the user, and could be used to directly renew his premium plan.
     */
    fun getRenewUrl(email: String) :String {
        val renew = Renew(email)
        renewRepository.saveAndFlush(renew)
        jobScheduler.schedule(Instant.now().plus(timeToRemindTheUserToRenew)) {
            forgetRenewUrl(renew.email)
        }
        return "http://localhost:4200/renew/${renew.url}"
    }

    /**
     * Looks for a URL in the database and returns his paired email.
     * @param[renewURL] the unique renewal URL that a user received via an email.
     * @return the email that the URL is paired with.
     */
    fun extractEmail(renewURL: String) :String? {
        return renewRepository.findByUrl(renewURL)?.email
    }

    // TODO: Switch back to sending emails

    /**
     * @param[email] the email that will receive an email reminding the user to renew his premium plan before his messages
     * will be deleted.
     */
    fun sendRenewWarning(email :String){
        /*val msg = SimpleMailMessage()
        msg.setTo(email)
        msg.setSubject("ChatApp - A reminder to renew your one-month premium plan")
        msg.setText("Hi,\n" +
                "We want to remind you to renew your one-month premium plan in the next two days, otherwise your messages might get deleted\n" +
                "Click this link if you want to renew right now ${getRenewUrl(email)}\n" +
                "Best regards, ChatApp")
        javaMailSender.send(msg)*/
        println("Hi,\n" +
                "We want to remind you to renew your one-month premium plan in the next two days, otherwise your messages might get deleted\n" +
                "Click this link if you want to renew right now ${getRenewUrl(email)}\n" +
                "Best regards, ChatApp")
    }

    /**
     * @param[email] the email that will receive an email letting the user know his premium plan was renewed automatically.
     */
    fun sendAutomaticallyRenewedNotification(email :String){
        /*val msg = SimpleMailMessage()
        msg.setTo(email)
        msg.setSubject("ChatApp - Your premium plan was automatically renewed")
        msg.setText("Hi,\n" +
                "We want to let you know your premium plan was automatically renewed\n" +
                "Best regards, ChatApp")
        javaMailSender.send(msg)*/
        println("Hi,\n" +
                "We want to let you know your premium plan was automatically renewed\n" +
                "Best regards, ChatApp")
    }

}