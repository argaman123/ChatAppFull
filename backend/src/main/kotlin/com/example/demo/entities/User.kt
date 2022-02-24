package com.example.demo.entities

import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import javax.persistence.*

/**
 * Represents an account of a user
 * @param[nickname] the nickname of the user, he will be identified by this name when writing in the chat
 * @param[email] the email of the user, he will connect to the chat using this email
 * @param[password] a hashed and encoded password of the user
 * @param[roles] the rules (authorization) of the user
 * @param[premium] the [Premium] plan this account is currently using, null if he is a free account
 */
@Entity
@Table(name="users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id :Long? = null,
    @Column(unique = true, length = 20) var nickname: String = "",
    @Column(unique = true, length = 30) val email: String = "",
    var password: String = "",
    val roles: String,

    @OneToOne(optional = true)
    @JoinColumn(name = "premium_id", referencedColumnName = "id")
    @Cascade(CascadeType.ALL)
    var premium: Premium? = null
)