package com.example.demo.repositories

import com.example.demo.entities.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByEmail(email: String): List<Notification>
    fun deleteByEmailAndType(email: String, type: String)
}