package com.example.demo.controllers

import com.example.demo.entities.User
import com.example.demo.auth.JwtUtil
import com.example.demo.services.PremiumBackgroundService
import com.example.demo.models.LoginDTO
import com.example.demo.models.RegisterDTO
import com.example.demo.models.GuestRequest
import com.example.demo.repositories.UserRepository
import com.example.demo.services.PremiumDataService
import com.example.demo.services.PremiumService
import com.example.demo.services.RealUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("auth")
class AuthController @Autowired constructor(
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: RealUserDetailsService,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder,
    private val premiumService: PremiumService
) {

    @PostMapping("/login")
    fun login(@RequestBody loginDTO: LoginDTO, res: HttpServletResponse): ResponseEntity<String> {
        try {
            authenticationManager.authenticate(loginDTO.getToken())
        } catch (e: BadCredentialsException) {
            return ResponseEntity.status(403).body("Incorrect email address or password.")
        }
        val userDetails = userDetailsService.loadUserByUsername(loginDTO.username)
        val jwt = jwtUtil.generateToken(userDetails.username)
        val cookie = Cookie("jwt", jwt.token)
        cookie.path = "/"
        //cookie.secure = true
        cookie.isHttpOnly = true
        res.addCookie(cookie)
        return ResponseEntity.ok(jwt.expiration)
    }

    @PostMapping("/guest")
    fun guest(@RequestBody guestRequest: GuestRequest, res: HttpServletResponse): ResponseEntity<String> {
        try {
            authenticationManager.authenticate(guestRequest.getToken())
        } catch (e: BadCredentialsException) {
            return ResponseEntity.status(403).body("Nickname is taken.")
        }
        val jwt = jwtUtil.generateToken(guestRequest.nickname, "guest")
        val cookie = Cookie("jwt", jwt.token)
        cookie.path = "/"
        //cookie.secure = true
        cookie.isHttpOnly = true
        res.addCookie(cookie)
        return ResponseEntity.ok(jwt.expiration)
    }

    @PostMapping("/register")
    fun register(@RequestBody registerDTO: RegisterDTO): ResponseEntity<String> {
        val errors = mutableListOf<String>()
        userRepository.findByEmail(registerDTO.email)?.let {
            errors.add("Email is already being used")
        }
        userRepository.findByNickname(registerDTO.nickname)?.let {
            errors.add("Nickname is already taken")
        }
        return if (errors.isEmpty()) {
            premiumService.switchPlan(
                User(
                    nickname = registerDTO.nickname,
                    email = registerDTO.email,
                    password = passwordEncoder.encode(registerDTO.password),
                    roles = "USER"
                ), registerDTO.premiumPlan
            )
            ResponseEntity.ok("success")
        } else
            ResponseEntity.status(409).body(errors.joinToString(separator = "\n"))
    }

    @GetMapping("/done")
    fun logout() {
    }
}