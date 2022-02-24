package com.example.demo.entities

import java.util.*
import javax.persistence.*

/**
 * Bundles a URL with an email that allows for a quick renewal just by clicking a link
 * @param[email] the email of the user
 */
@Entity
@Table(name = "renew_urls")
data class Renew(
    val email: String,
    val url: String = UUID.randomUUID().toString(),
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
)