package com.example.demo.entities

import javax.persistence.*

@Entity
@Table(name = "premium_jobs")
data class Job(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)  val id: Long? = null,
    @Column(name = "self_id") val selfID: String,
    @Column(name = "job_id") val jobID: String,
    val type: String,
    val email: String
)
