package com.example.demo.repository

import com.example.demo.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun findByEmail(email: String): User?
    fun findByNickname(nickname: String) : User?
}