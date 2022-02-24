package com.example.demo.entities

import java.util.*
import javax.persistence.*

/**
 * Represents a notification that was sent to the user
 * @param[email] the email of the user the notification is for
 * @param[content] the contents of the notification
 * @param[type] a short description of the notification, which helps to find it later if needed
 * @param[locked] if true, the notification could not be deleted by the user, only by the program itself
 * @param[datetime] the time of which the notification was created
 */
@Entity
@Table(name = "notifications")
data class Notification(
    val email: String,
    val content: String,
    val type: String,
    val locked: Boolean = false,
    val datetime: Date = Date(),
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id :Long? = null,
)