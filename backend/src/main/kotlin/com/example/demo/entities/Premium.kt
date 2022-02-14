package com.example.demo.entities

import java.time.LocalDate
import java.util.*
import javax.persistence.*


@Entity
@Table(name="premium")
data class Premium (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id :Long? = null,
    var expiration: Date,
    var plan: String,
    @OneToOne(mappedBy = "premium") val user: User
)