package com.example.demo.repositories

import com.example.demo.entities.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<Message, Long>{
    fun countByEmail(email :String) :Long?
}