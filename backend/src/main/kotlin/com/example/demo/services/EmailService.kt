package com.example.demo.services

import com.example.demo.entities.Renew
import com.example.demo.repositories.RenewRepository
import com.example.demo.static.timeToRemindTheUserToRenew
import org.jobrunr.jobs.annotations.Job
import org.jobrunr.scheduling.JobScheduler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.crypto.encrypt.Encryptors
import org.springframework.security.crypto.keygen.KeyGenerators
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit


@Service
class EmailService @Autowired constructor(
    private val javaMailSender: JavaMailSender,
    private val jobScheduler: JobScheduler,
    private val renewRepository: RenewRepository
) {

    @Transactional
    @Job(name = "Invalidate the renew link")
    fun forgetRenewUrl(email: String){
        renewRepository.deleteByEmail(email)
    }

    fun getRenewUrl(email: String) :String {
        val renew = Renew(email)
        renewRepository.saveAndFlush(renew)
        jobScheduler.schedule(Instant.now().plus(timeToRemindTheUserToRenew)) {
            forgetRenewUrl(renew.email)
        }
        return "http://localhost:4200/renew/${renew.url}"
    }

    fun extractEmail(renewURL: String) :String? {
        return renewRepository.findByUrl(renewURL)?.email
    }

    fun sendRenewWarning(email :String, renewURL :String? = null){
        /*val msg = SimpleMailMessage()
        msg.setTo(email)
        msg.setSubject("ChatApp - A reminder to renew your one-month premium plan")
        msg.setText("Hi,\n" +
                "We want to remind you to renew your one-month premium plan in the next two days, otherwise your messages might get deleted\n" +
                "Click this link if you want to renew right now "+ (renewURL ?: getRenewUrl(email)) + "\n" +
                "Best regards, ChatApp")
        javaMailSender.send(msg)*/
        println("Hi,\n" +
                "We want to remind you to renew your one-month premium plan in the next two days, otherwise your messages might get deleted\n" +
                "Click this link if you want to renew right now "+ (renewURL ?: getRenewUrl(email)) + "\n" +
                "Best regards, ChatApp")
    }

    fun sendRenewedNotification(email :String){
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