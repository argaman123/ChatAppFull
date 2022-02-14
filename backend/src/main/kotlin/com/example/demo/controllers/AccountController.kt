package com.example.demo.controllers

import com.example.demo.entities.Premium
import com.example.demo.entities.User
import com.example.demo.models.ChangePasswordDTO
import com.example.demo.models.ChatUser
import com.example.demo.models.PremiumDTO
import com.example.demo.repositories.PremiumRepository
import com.example.demo.repositories.UserRepository
import com.example.demo.services.RealUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.*


// Actions you can take after you are logged in
@RestController
@RequestMapping("account")
class AccountController @Autowired constructor(
    private val userRepository: UserRepository,
    private val premiumRepository: PremiumRepository,
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
            // TODO: make sure plan is different than the current one, even though it should be allowed at frontend
            if (it.premium == null){
                val cal = Calendar.getInstance()
                cal.add(Calendar.MINUTE, 10)
                it.premium = Premium(expiration = Date.from(cal.toInstant()), plan = plan, user = it)
            } else {
                if (it.premium?.plan == plan){ // renew
                    Calendar.getInstance().time = it.premium?.expiration
                    val newExp = Calendar.getInstance()
                    newExp.time = it.premium?.expiration
                    newExp.add(Calendar.MINUTE, 10)
                    it.premium?.expiration = Date.from(newExp.toInstant())
                }
                it.premium?.plan = plan
            }
            it.premium?.let { premium -> premiumRepository.saveAndFlush(premium) }
            userRepository.saveAndFlush(it) // ? Maybe optional in case premium already existed
            return ResponseEntity.ok(PremiumDTO(it.premium?.expiration, it.premium?.plan ?: "none"))
        }
        return ResponseEntity.status(403).body(null)
    }
}