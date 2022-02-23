package com.example.demo.services

import com.example.demo.entities.Notification
import com.example.demo.entities.User
import com.example.demo.models.RealUser
import com.example.demo.repositories.NotificationRepository
import com.example.demo.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import javax.transaction.Transactional

@Service
class NotificationService @Autowired constructor(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    private val messagingTemplate: SimpMessagingTemplate,

    ) {

    companion object {
        const val renewWarningType = "renew warning"
        const val renewedAutomaticallyType = "renewed automatically"
    }

    fun notify(notification: Notification) {
        userRepository.findByEmail(notification.email)?.let {
            notify(it, notification)
        }
    }

    fun notify(user: User, notification: Notification) {
        notificationRepository.saveAndFlush(notification)
        // RealUser(it, userRepository).getID() == email, but just in case I change the implementation in the future
        messagingTemplate.convertAndSendToUser(
            RealUser(user, userRepository).getID(),
            "/topic/notifications",
            notification
        )
    }

    fun notifyRenewWarning(email: String) {
        userRepository.findByEmail(email)?.let {
            notify(
                it,
                Notification(
                    email,
                    "You must renew your one-month premium before " + SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(it.premium?.expiration)
                            + ", or else your messages will be deleted.",
                    renewWarningType,
                    true
                )
            )
        }
    }

    @Transactional
    fun deleteOldWarnings(email: String) {
        notificationRepository.deleteByEmailAndType(email, renewWarningType)
    }

    fun notifyRenewedAutomatically(email: String) {
        notify(
            Notification(
                email,
                "Your premium subscription was automatically renewed. Enjoy!",
                renewedAutomaticallyType
            )
        )
    }

    fun tryDeletingNotification(email: String, id: Long): Boolean {
        val notificationOptional = notificationRepository.findById(id)
        if (notificationRepository.findById(id).isPresent) {
            val notification = notificationOptional.get()
            if (notification.locked || notification.email != email) return false
            notificationRepository.delete(notification)
        }
        return true
    }

}