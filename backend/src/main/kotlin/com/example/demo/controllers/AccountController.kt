package com.example.demo.controllers

import com.example.demo.services.PremiumBackgroundService
import com.example.demo.models.ChangePasswordDTO
import com.example.demo.models.ChatUser
import com.example.demo.models.PremiumDTO
import com.example.demo.repositories.UserRepository
import com.example.demo.services.EmailService
import com.example.demo.services.PremiumDataService
import com.example.demo.services.PremiumService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*


// Actions you can take after you are logged in
@RestController
@RequestMapping("account")
class AccountController @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val premiumService: PremiumService,
    private val emailService: EmailService
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

    @GetMapping("/premium")
    fun getPremiumPlan(auth: Authentication) :ResponseEntity<PremiumDTO> {
        (auth.principal as ChatUser).getEmail()?.let { userRepository.findByEmail(it) }?.premium?.let {
            return ResponseEntity.ok(PremiumDTO(it.expiration, it.plan))
        } ?: run {
            return ResponseEntity.ok(PremiumDTO(plan = "none"))
        }
    }

    @PutMapping("/premium")
    fun setPremiumPlan(auth: Authentication, @RequestBody plan: String) :ResponseEntity<PremiumDTO?>{
        (auth.principal as ChatUser).getEmail()?.let { userRepository.findByEmail(it) }?.let {
            premiumService.switchPlan(it, plan)
            return ResponseEntity.ok(PremiumDTO(it.premium?.expiration, it.premium?.plan ?: "none"))
        }
        return ResponseEntity.status(403).body(null)
    }

    @PutMapping("/renew")
    fun renew(@RequestBody code: String) :ResponseEntity<String>{
        val email = emailService.extractEmail(code)
        email?.let {
            userRepository.findByEmail(email)?.let {
                premiumService.renew(it)
                return ResponseEntity.ok("Your one-month plan was renewed successfully")
            }
        }
        return ResponseEntity.status(403).body("Failed to renew. Please try renewing manually from the app.")
    }
}