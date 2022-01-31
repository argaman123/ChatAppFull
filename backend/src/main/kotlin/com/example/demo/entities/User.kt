package com.example.demo.entities

import javax.persistence.*

@Entity
@Table(name="users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id :Long,
    @Column(unique = true, length = 20) val nickname: String = "",
    @Column(unique = true, length = 30) val email: String = "",
    val password: String = "",
    val roles: String
)