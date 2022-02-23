package com.example.demo.models

import com.example.demo.entities.Notification
import java.util.Date

data class NotificationDTO(val content: String, val id: Long, val dateTime: Date, val locked: Boolean) {
    constructor(notification: Notification) : this(notification.content, notification.id!!, notification.datetime, notification.locked)
}