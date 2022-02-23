package com.example.demo.entities

import java.util.*
import javax.persistence.*

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