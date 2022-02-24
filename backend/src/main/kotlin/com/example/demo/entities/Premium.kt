package com.example.demo.entities

import java.util.*
import javax.persistence.*

/**
 * Represents the current premium plan for a user
 * @param[expiration] the expiration date of the current plan
 * @param[plan] the name of the current plan
 * @param[user] the [User] of which the premium plan is for
 */
@Entity
@Table(name="premium")
data class Premium (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id :Long? = null,
    var expiration: Date,
    var plan: String,
    @OneToOne(mappedBy = "premium") val user: User
)