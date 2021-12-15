package com.example.demo.dao

import com.example.demo.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepo : JpaRepository<User, Long> {
    fun findUserByUsername(username: String): User
}