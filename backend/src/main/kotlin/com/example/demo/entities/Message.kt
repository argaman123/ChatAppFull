package com.example.demo.entities

import java.util.*
import javax.persistence.*


@Entity
@Table(name="messages")
data class Message(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id :Long,
    val nickname: String = "",
    val email: String? = "",
    val datetime: Date = Date(),
    val content: String = ""
)
