package com.example.demo.repositories

import com.example.demo.entities.Job
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JobRepository : JpaRepository<Job, Long> {
    fun findByJobID(jobID :String) :Job
    fun findByEmail(email: String) :List<Job>
    fun findByEmailAndTypeContaining(email: String, type: String) :List<Job>
    fun deleteBySelfID(selfID: String) :Long
    fun findBySelfID(selfID: String) :Job?
}