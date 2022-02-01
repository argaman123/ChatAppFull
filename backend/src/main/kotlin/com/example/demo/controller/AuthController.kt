package com.example.demo.controller

import com.example.demo.entities.User
import com.example.demo.jwt.JwtUtil
import com.example.demo.models.AuthenticationRequest
import com.example.demo.models.Credentials
import com.example.demo.repository.UserRepository
import com.example.demo.service.RealUserDetailsService
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
    private val passwordEncoder: PasswordEncoder
) {
    @PostMapping("/login")
    fun login(@RequestBody authenticationRequest: AuthenticationRequest, res : HttpServletResponse): ResponseEntity<String> {
        try {
            authenticationManager.authenticate(authenticationRequest.getToken())
        } catch (e : BadCredentialsException){
            return ResponseEntity.status(403).body("Bad credentials")
        }
        val userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username)
        val jwt = jwtUtil.generateToken(userDetails)
        val cookie = Cookie("jwt", jwt.token)
        cookie.path = "/"
        //cookie.secure = true
        cookie.isHttpOnly = true
        res.addCookie(cookie)
        return ResponseEntity.ok(jwt.expiration)
    }

    @PostMapping("/register")
    fun register(@RequestBody credentials: Credentials): ResponseEntity<String> {
        val errors = mutableListOf<String>()
        userRepository.findByEmail(credentials.email)?.let {
            errors.add("Email is already being used")
        }
        userRepository.findByNickname(credentials.nickname)?.let {
            errors.add("Nickname is already taken")
        }
        return if (errors.isEmpty()) {
            userRepository.saveAndFlush(
                User(
                    nickname = credentials.nickname,
                    email = credentials.email,
                    password = passwordEncoder.encode(credentials.password),
                    roles = "USER"
                )
            )
            ResponseEntity.ok("success")
        } else
            ResponseEntity.status(409).body(errors.joinToString(separator = "\n"))
    }

    @GetMapping("/done")
    fun logout() {
    }
}