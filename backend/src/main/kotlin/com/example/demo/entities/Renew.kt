package com.example.demo.entities

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "renew_urls")
data class Renew(
    val email: String,
    val url: String = UUID.randomUUID().toString(),
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
)