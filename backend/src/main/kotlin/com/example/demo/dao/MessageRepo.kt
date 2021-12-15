package com.example.demo.dao

import com.example.demo.entities.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepo : JpaRepository<Message, Long>