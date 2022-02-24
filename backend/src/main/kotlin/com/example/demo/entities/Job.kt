package com.example.demo.entities

import javax.persistence.*
import org.jobrunr.jobs.JobId

/**
 * Represents a background job that was scheduled to do a task for a certain user
 * Includes extra information to help find a certain job.
 * @param[selfID] a random unique id which allows a job find and delete its own row from the database
 * @param[jobID] the [JobId] of the scheduled job
 * @param[type] the name of the function that the job will perform
 * @param[email] the email of the user that the task is scheduled for
 */
@Entity
@Table(name = "premium_jobs")
data class Job(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)  val id: Long? = null,
    @Column(name = "self_id") val selfID: String,
    @Column(name = "job_id") val jobID: String,
    val type: String,
    val email: String
)
