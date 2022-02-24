package com.example.demo.services

import com.example.demo.entities.Notification
import com.example.demo.entities.User
import com.example.demo.models.RealUser
import com.example.demo.repositories.NotificationRepository
import com.example.demo.repositories.UserRepository
import com.example.demo.responses.NotificationResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import javax.transaction.Transactional

/**
 * A service that allows to send a [Notification] to a [RealUser], keep track of those that were already sent, and delete
 * some when needed- either manually by the user, or when a [Notification] is no longer valid.
 */
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

    /**
     * Extracts the email from the [Notification] object, and uses it to send a [User] to [notify] along with
     * the [notification] itself.
     * @param[notification] contains all the information about the notification, to allow for easier lookup when needed.
     */
    fun notify(notification: Notification) {
        userRepository.findByEmail(notification.email)?.let {
            notify(it, notification)
        }
    }

    /**
     * Saves a notification in the database and pushes it inside a [NotificationResponse] directly to a [RealUser].
     * @param[user] the [User] that the notification is for.
     * @param[notification] contains all the information about the notification, to allow for easier lookup when needed.
     */
    fun notify(user: User, notification: Notification) {
        notificationRepository.saveAndFlush(notification)
        // RealUser(it, userRepository).getID() == email, but just in case I change the implementation in the future
        messagingTemplate.convertAndSendToUser(
            RealUser(user, userRepository).getID(),
            "/topic/notifications",
            NotificationResponse(notification)
        )
    }

    /**
     * Notifies a [RealUser] that he needs to renew his premium plan before it expires and causes his messages to be deleted.
     * @param[email] the email of the said user.
     */
    fun notifyRenewWarning(email: String) {
        userRepository.findByEmail(email)?.let {
            notify(
                it,
                Notification(
                    email,
                    "You must renew your one-month premium before " + SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(it.premium?.expiration)
                            + ", or else your messages will be deleted.",
                    renewWarningType,
                    true
                )
            )
        }
    }

    /**
     * Allows the [PremiumBackgroundService] to delete invalid notifications when the [RealUser] switches his premium plan,
     * or renews his current one.
     */
    @Transactional
    fun deleteOldWarnings(email: String) {
        notificationRepository.deleteByEmailAndType(email, renewWarningType)
    }

    /**
     * Notifies a [RealUser] that he needs his premium plan was automatically renewed.
     * @param[email] the email of the said user.
     */
    fun notifyRenewedAutomatically(email: String) {
        notify(
            Notification(
                email,
                "Your premium subscription was automatically renewed. Enjoy!",
                renewedAutomaticallyType
            )
        )
    }

    /**
     * Allows a [RealUser] to delete his own non-locked [Notification].
     * @param[email] the email of the said [RealUser].
     * @param[id] the id of the said [Notification].
     * @return true if the [Notification] was successfully deleted, false otherwise (for example, if the [Notification]
     * is locked or is someone else's).
     */
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