package com.example.demo.controllers

import com.example.demo.entities.User
import com.example.demo.models.ChangePasswordDTO
import com.example.demo.models.ChatUser
import com.example.demo.repositories.UserRepository
import com.example.demo.services.RealUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*


// Actions you can take after you are logged in
@RestController
@RequestMapping("account")
class AccountController @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @PutMapping("/nickname")
    fun changeNickname(auth: Authentication, @RequestBody nickname: String): ResponseEntity<String> {
        val user = auth.principal as ChatUser
        userRepository.findByNickname(user.getNickname())?.let {
            userRepository.findByNickname(nickname)?.let {
                return ResponseEntity.status(403).body("Nickname is taken.")
            } ?: run {
                it.nickname = nickname
                userRepository.saveAndFlush(it)
                return ResponseEntity.ok("Nickname was changed successfully")
            }
        } ?: run {
            return ResponseEntity.status(403).body("User not found.")
        }
    }

    @PutMapping("/password")
    fun changePassword(auth: Authentication, @RequestBody passwords: ChangePasswordDTO): ResponseEntity<String> {
        val user = auth.principal as ChatUser
        userRepository.findByNickname(user.getNickname())?.let {
            return if (passwordEncoder.matches(passwords.oldPassword, it.password)){
                it.password = passwordEncoder.encode(passwords.newPassword)
                userRepository.saveAndFlush(it)
                ResponseEntity.ok("Password was changed successfully")
            } else {
                ResponseEntity.status(403).body("Password is incorrect.")
            }
        } ?: run {
            return ResponseEntity.status(403).body("User not found.")
        }
    }


}