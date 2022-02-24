package com.example.demo.controllers

import com.example.demo.requests.ChangePasswordRequest
import com.example.demo.models.ChatUser
import com.example.demo.responses.PremiumResponse
import com.example.demo.repositories.UserRepository
import com.example.demo.services.EmailService
import com.example.demo.services.PremiumService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*


/**
 * This controller maps actions that you can only take if you have a real account
 */
@RestController
@RequestMapping("account")
class AccountController @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val premiumService: PremiumService,
    private val emailService: EmailService,
) {

    /**
     * allows a registered user to change his nickname
     * @param[nickname] the new nickname
     * @return A [ResponseEntity] that holds a [String].
     *
     * If the nickname was changed successfully returns [ResponseEntity.ok].
     * Else, returns what went wrong in the process (status 403).
     */
    @PutMapping("/nickname")
    fun changeNickname(auth: Authentication, @RequestBody nickname: String): ResponseEntity<String> {
        val user = auth.principal as ChatUser
        // Looks for the user in the database
        userRepository.findByNickname(user.getNickname())?.let {
            // checks to see if the new username is taken
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

    /**
     * allows a registered user to change his password
     * @param[passwords] the current and new passwords
     * @return A [ResponseEntity] that holds a [String].
     *
     * If the password was changed successfully returns [ResponseEntity.ok].
     * Else, returns what went wrong in the process (status 403).
     */
    @PutMapping("/password")
    fun changePassword(auth: Authentication, @RequestBody passwords: ChangePasswordRequest): ResponseEntity<String> {
        val user = auth.principal as ChatUser
        // Looks for the user in the database
        userRepository.findByNickname(user.getNickname())?.let {
            // Verifies that the old password entered by the user is correct
            return if (passwordEncoder.matches(passwords.oldPassword, it.password)) {
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

    /**
     * @return A [ResponseEntity] of a [PremiumResponse] which holds the current premium plan name and expiration date.
     */
    @GetMapping("/premium")
    fun getPremiumPlan(auth: Authentication): ResponseEntity<PremiumResponse> {
        // Looks for the user in the database and tries to find his premium plan
        (auth.principal as ChatUser).getEmail()?.let { userRepository.findByEmail(it) }?.premium?.let {
            return ResponseEntity.ok(PremiumResponse(it.expiration, it.plan))
        } ?: run {
            return ResponseEntity.ok(PremiumResponse(plan = "none"))
        }
    }

    /**
     * allows a registered user to switch his premium plan
     * @param[plan] the requested plan name. In cases where the new plan is the same as the current one, renews it.
     * @return A [ResponseEntity] of a [PremiumResponse] which holds the new premium plan name and expiration date,
     * in case everything went right. Otherwise, returns what went wrong in the process (status 403).
     */
    @PutMapping("/premium")
    fun setPremiumPlan(auth: Authentication, @RequestBody plan: String): ResponseEntity<PremiumResponse?> {
        // Looks for the user in the database and if it was found switches its premium plan and returns the new one
        // Note, that the user will reload the page anyway in order to refresh the Principal which holds his old roles
        // and thus might not allow him to send messages. But, I still figured I would return it just in case of a new
        // implementation that refreshes the Principal without forcing the user to reload the page (such as a refresh token)
        (auth.principal as ChatUser).getEmail()?.let { userRepository.findByEmail(it) }?.let {
            premiumService.switchPlan(it, plan)
            return ResponseEntity.ok(PremiumResponse(it.premium?.expiration, it.premium?.plan ?: "none"))
        }
        return ResponseEntity.status(403).body(null)
    }

    /**
     * Can be accessed by anyone who has the link and allows for an immediate renewal
     * In the future it would probably have a proper check to see if the payment goes through successfully
     * @param[code] a unique id that represents the email of the user who wants to renew his current plan.
     * @return A [ResponseEntity] that holds a [String].
     *
     * If the current plan was renewed successfully returns [ResponseEntity.ok].
     * Else, returns status 403.
     */
    @PutMapping("/renew")
    fun renew(@RequestBody code: String): ResponseEntity<String> {
        // Looks for the code in the "Renew" database and gets the email that was paired with it
        val email = emailService.extractEmail(code)
        email?.let {
            // Looks for the user in the database and if found renews his current plan
            userRepository.findByEmail(email)?.let {
                premiumService.renew(it)
                return ResponseEntity.ok("Your one-month plan was renewed successfully")
            }
        }
        return ResponseEntity.status(403).body("Failed to renew. Please try renewing manually from the app.")
    }

}