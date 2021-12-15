package com.example.demo.service

import com.example.demo.dao.UserRepo
import com.example.demo.dtos.UserDto
import com.example.demo.entities.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService (
    private val userRepo: UserRepo,
    private val passwordEncoder: PasswordEncoder
    ) {
    fun findUserByUsername(username: String): User? {
        return userRepo.findUserByUsername(username)
    }

    fun saveUser(dto: UserDto) {
        userRepo.save(User(dto, passwordEncoder))
    }

}