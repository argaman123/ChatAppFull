package com.example.demo.repositories

import com.example.demo.entities.Renew
import org.springframework.data.jpa.repository.JpaRepository

interface RenewRepository : JpaRepository<Renew, Long> {
    fun findByEmail(email: String) :Renew?
    fun findByUrl(url: String) :Renew?
    fun deleteByEmail(email :String)
}